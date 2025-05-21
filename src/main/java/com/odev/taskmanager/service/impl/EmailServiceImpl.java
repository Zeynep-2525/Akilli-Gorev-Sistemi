package com.odev.taskmanager.service.impl;

import com.odev.taskmanager.service.EmailService;
import com.odev.taskmanager.service.MotivationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MotivationService motivationService;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, MotivationService motivationService) {
        this.mailSender = mailSender;
        this.motivationService = motivationService;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {  // Metod ismini 3 parametreli olarak güncelledik
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("senanazkaya0@gmail.com");
        mailSender.send(message);
    }
}
