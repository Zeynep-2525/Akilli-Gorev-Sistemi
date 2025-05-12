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
    public void sendReminderEmail(String to, String taskDetails) {
        String quote = motivationService.getMotivationalQuote();
        String subject = "Task Reminder";
        String body = "Don't forget your task: " + taskDetails + "\n\n" +
                      " Motivation of the Day:\n" + quote;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
