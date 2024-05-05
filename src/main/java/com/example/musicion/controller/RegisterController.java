package com.example.musicion.controller;

import com.example.musicion.model.auth.User;
import com.example.musicion.repository.UserRepository;
import com.example.musicion.service.EmailValidateService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-api")
public class RegisterController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailValidateService emailValidateService;

    @Autowired
    PasswordEncoder encoder;

    @Transactional
    @PostMapping("/register")
    @Tag(name = "Регистрация пользователя", description = "Создание неактивированного пользователя и отправка письма с подтв.")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан. Письмо с подтверждением отправлено")
    @ApiResponse(responseCode = "409", description = "Пользователь с таким именем найден, регистрация невозможна")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    public ResponseEntity<HttpStatus> register(@RequestBody User user) {
        try {
            if (userRepository.findByUsername(user.getUsername()).isPresent())
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            emailValidateService.createEmailValidate(user.getUsername(), user.getEmail());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
