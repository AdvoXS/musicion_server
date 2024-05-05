package com.example.musicion.repository;

import com.example.musicion.model.auth.EmailValidate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmailValidateRepository extends CrudRepository<EmailValidate, Long> {
    List<EmailValidate> findAllByUsername(String username);
}
