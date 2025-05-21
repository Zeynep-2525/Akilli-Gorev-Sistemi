package com.odev.taskmanager.scheduler;

import com.odev.taskmanager.service.EmailService;
import com.odev.taskmanager.service.MotivationService;
import com.odev.taskmanager.service.TaskService;
import com.odev.taskmanager.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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

    // Her sabah saat 08:00'de çalışır
    @Scheduled(cron = "0 0 8 * * *")
    public void sendMorningReminders() {
        log.info("Hatırlatma gönderme işlemi başlatıldı.");

        List<Task> highPriorityTasks = taskService.getHighPriorityTasks();

        if (highPriorityTasks.isEmpty()) {
            log.info("Öncelikli görev bulunamadı, e-posta gönderimi yapılmadı.");
            return;
        }

        String quote = motivationService.getDailyQuote(); // Motivasyon sözü çek
        log.info("Günün motivasyon sözü alındı.");

        for (Task task : highPriorityTasks) {
            String message = String.format(
                    "Görev: %s\nAçıklama: %s\n\nGünün Sözü: %s",
                    task.getTitle(),
                    task.getDescription(),
                    quote
            );

            try {
                emailService.sendEmail(task.getUserEmail(), "Öncelikli Görev Hatırlatması", message);
                log.info("Hatırlatma e-postası gönderildi: Kullanıcı={}", task.getUserEmail());
            } catch (Exception e) {
                log.error("E-posta gönderilemedi: Kullanıcı={}, Hata={}", task.getUserEmail(), e.getMessage());
            }
        }

        log.info("Hatırlatma gönderme işlemi tamamlandı. Görev sayısı: {}", highPriorityTasks.size());
    }
}
