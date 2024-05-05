package com.example.musicion.service;

import com.example.musicion.model.auth.EmailValidate;
import com.example.musicion.repository.EmailValidateRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmailValidateService {
    @Autowired
    EmailValidateRepository emailValidateRepository;

    @Autowired
    MailSenderService mailSenderService;

    @Value("${server_address}")
    private String serverAddress;

    @Value("${server_port}")
    private int serverPort;

    public EmailValidate createEmailValidate(String userName, String email) {
        List<EmailValidate> validateRecords = emailValidateRepository.findAllByUsername(userName);
        long currentTime = System.currentTimeMillis();
        Date currentDate = new Date(currentTime);
        validateRecords.stream().filter(e -> e.getExpired().compareTo(new Date(currentTime)) > 0).forEach(e -> {
            e.setExpired(new java.sql.Date(currentTime));
            emailValidateRepository.save(e);
        });
        EmailValidate emailValidate = createEmailValidate(userName, currentDate);
        emailValidate = emailValidateRepository.saveAndFlush(emailValidate);
        sendEmailValidate(email, emailValidate.getCode());
        return emailValidate;
    }

    private void sendEmailValidate(String email, Long code) {
        mailSenderService.sendSimpleMessage(email, "Подтверждение аккаунта musicion", "Подтвердите аккаунт по ссылке" +
                " http:\\\\" + serverAddress + ":" + serverPort + "/mail/validate?code=" + code);
    }

    private EmailValidate createEmailValidate(String userName, Date currentDate) {
        EmailValidate emailValidate = new EmailValidate();
        emailValidate.setUsername(userName);
        emailValidate.setExpired(new java.sql.Date(DateUtils.addDays(currentDate, 1).getTime()));
        emailValidate.setActivate(false);
        return emailValidate;
    }
}
