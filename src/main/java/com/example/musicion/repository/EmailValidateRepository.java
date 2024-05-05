package com.example.musicion.repository;

import com.example.musicion.model.auth.EmailValidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailValidateRepository extends JpaRepository<EmailValidate, Long> {
    List<EmailValidate> findAllByUsername(String username);
}
