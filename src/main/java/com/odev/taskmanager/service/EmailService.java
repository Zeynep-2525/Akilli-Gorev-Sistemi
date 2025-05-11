package com.odev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MotivationService motivationService;

    public void sendReminderEmail(String to, String taskDetails) {
        String quote = motivationService.getMotivationalQuote();
        String subject = "Task Reminder";
        String body = "Don't forget your task: " + taskDetails + "\n\n" +
                      "💡 Motivation of the Day:\n" + quote;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
