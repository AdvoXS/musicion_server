package com.example.musicion.controller;

import com.example.musicion.model.auth.User;
import com.example.musicion.override.AppError;
import com.example.musicion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/open-api")
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/checkUser")
    public ResponseEntity<?> findUser(@RequestBody String username) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),"Неправильно сформирован запрос"),
                    HttpStatus.BAD_REQUEST);
        }
        Optional<User> foundedUser = userRepository.findByUsername(username);
        if (foundedUser.isPresent()) {
            return new ResponseEntity<>(foundedUser.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(),
                "Пользователь '" + username + "' не найден"), HttpStatus.NOT_FOUND);
    }
}
