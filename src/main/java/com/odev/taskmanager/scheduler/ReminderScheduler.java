package com.odev.taskmanager.scheduler;

import com.odev.taskmanager.service.EmailService;
import com.odev.taskmanager.service.TaskService;
import com.odev.taskmanager.model.Task;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderScheduler {

    private final TaskService taskService;
    private final EmailService emailService;

    public ReminderScheduler(TaskService taskService, EmailService emailService) {
        this.taskService = taskService;
        this.emailService = emailService;
    }

    // Her sabah saat 08:00'de çalışır
    @Scheduled(cron = "0 0 8 * * *")
    public void sendMorningReminders() {
        List<Task> highPriorityTasks = taskService.getHighPriorityTasks();

        for (Task task : highPriorityTasks) {
            String message = "Görev: " + task.getTitle() + "\nAçıklama: " + task.getDescription();
            emailService.sendEmail(task.getUserEmail(), "Öncelikli Görev Hatırlatması", message);
        }
    }
}
