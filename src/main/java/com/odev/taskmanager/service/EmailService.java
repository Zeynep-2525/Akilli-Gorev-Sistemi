package com.odev.taskmanager.service;

public interface EmailService {
    void sendReminderEmail(String to, String taskDetails);
}
