package com.odev.taskmanager.scheduler;

import com.odev.taskmanager.service.EmailService;

import com.odev.taskmanager.service.MotivationService;
import com.odev.taskmanager.service.TaskService;
import com.odev.taskmanager.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




@Component
public class ReminderScheduler {

	private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final TaskService taskService;
    private final EmailService emailService;
    private final MotivationService motivationService;

    public ReminderScheduler(TaskService taskService, EmailService emailService, MotivationService motivationService) {
        this.taskService = taskService;
        this.emailService = emailService;
        this.motivationService = motivationService;
    }

    // Her sabah saat 08:00'de çalışır  * 0 8 **
    @Scheduled(cron = "0 8 * * * *")
    public void sendMorningReminders() {
        log.info("Zamanlayıcı çalıştı: Sabah hatırlatmaları gönderiliyor...");

        List<Task> highPriorityTasks = taskService.getHighPriorityTasks();
        String quote = motivationService.getDailyQuote(); // Motivasyon sözü çek

        for (Task task : highPriorityTasks) {
            String message = String.format(
                " Görev: %s\n Açıklama: %s\n\n Günün Sözü: %s",
                task.getTitle(),
                task.getDescription(),
                quote
            );
            emailService.sendEmail(task.getUserEmail(), " Öncelikli Görev Hatırlatması", message);
        }

        log.info(" Hatırlatma e-postaları gönderildi. Görev sayısı: {}", highPriorityTasks.size());
    }
}
