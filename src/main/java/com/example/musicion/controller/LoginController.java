package com.example.musicion.controller;

import com.example.musicion.model.auth.User;
import com.example.musicion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/open-api")
public class LoginController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/checkUser")
    public ResponseEntity<HttpStatus> findUser(@RequestParam String username) {
        if (username.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<User> foundedUser = userRepository.findByUsername(username);
        if (foundedUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
