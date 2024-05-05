package com.example.musicion.controller;

import com.example.musicion.model.auth.EmailValidate;
import com.example.musicion.model.auth.User;
import com.example.musicion.repository.EmailValidateRepository;
import com.example.musicion.repository.UserRepository;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mail")
public class EmailValidateController {
    @Autowired
    EmailValidateRepository emailValidateRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${client_address}")
    private String clientAddress;

    @Value("${client_port}")
    private String clientPort;

    @PostMapping("/validate")
    @NotBlank
    @ApiResponse(responseCode = "200", description = "Письмо отправлено")
    @ApiResponse(responseCode = "404", description = "Пользователь из тела запроса не найден")
    @ApiResponse(responseCode = "409", description = "Пользователь из тела запроса уже активирован")
    @Tag(name = "Валидация почты", description = "Отправляет сообщение с ссылкой на подтверждение на почту")
    public ResponseEntity<HttpStatus> sendValidateEmail(@RequestBody UserNameData userNameData) {
        String userName = userNameData.userName;
        Optional<User> userOptional = userRepository.findByUsername(userName);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        if (user.isEnabled())
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        List<EmailValidate> validateRecords = emailValidateRepository.findAllByUsername(userName);
        long currentTime = System.currentTimeMillis();
        Date currentDate = new Date(currentTime);
        validateRecords.stream().filter(e -> e.getExpired().compareTo(new Date(currentTime)) > 0).forEach(e -> {
            e.setExpired(new java.sql.Date(currentTime));
            emailValidateRepository.save(e);
        });
        EmailValidate emailValidate = createEmailValidate(userName, currentDate);
        emailValidateRepository.save(emailValidate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private EmailValidate createEmailValidate(String userName, Date currentDate) {
        EmailValidate emailValidate = new EmailValidate();
        emailValidate.setUsername(userName);
        emailValidate.setExpired(new java.sql.Date(DateUtils.addDays(currentDate, 1).getTime()));
        emailValidate.setActivate(false);
        return emailValidate;
    }

    @GetMapping(value = "/validate")
    public ModelAndView getValidateEmail(@RequestParam(value = "code") Long code) {
        ResponseEntity<HttpStatus> entity;
        if (code == null || code < 0) {
            entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            ModelAndView modelAndView = new ModelAndView("validateEmail");
            modelAndView.addObject(entity);
            return modelAndView;
        }
        Optional<EmailValidate> validate = emailValidateRepository.findById(code);
        if (validate.isPresent()) {
            EmailValidate emailValidate = validate.get();
            if (emailValidate.getExpired().compareTo(new Date(System.currentTimeMillis())) < 0) {
                entity = new ResponseEntity<>(HttpStatus.GONE);
                ModelAndView modelAndView = new ModelAndView("validateEmail");
                modelAndView.addObject(entity);
                return modelAndView;
            }
            if (emailValidate.isActivate())
                entity = new ResponseEntity<>(HttpStatus.GONE);
            else {
                Optional<User> userOptional = userRepository.findByUsername(emailValidate.getUsername());
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setEnabled(true);
                    userRepository.save(user);
                    emailValidate.setActivate(true);
                    emailValidateRepository.save(emailValidate);
                    entity = new ResponseEntity<>(HttpStatus.OK);
                } else
                    entity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else
            entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        ModelAndView modelAndView = new ModelAndView("validateEmail");
        modelAndView.addObject("entity", entity);
        modelAndView.addObject("client_address", clientAddress);
        modelAndView.addObject("client_port", clientPort);
        return modelAndView;
    }
}

@Getter
@Setter
class UserNameData {
    String userName;
}
