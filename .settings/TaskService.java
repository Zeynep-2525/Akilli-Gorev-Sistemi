package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final MailService mailService;

    public TaskService(TaskRepository taskRepository, MailService mailService) {
        this.taskRepository = taskRepository;
        this.mailService = mailService;
    }

    // Yeni görev ekleme (öncelik yüksekse e-posta gönderir)
    public Task addTask(Task task) {
        Task savedTask = taskRepository.save(task);

        if (task.getPriority() >= 4) {
            try {
                mailService.sendSimpleMail(
                        "senanazkaya0@email.com",
                        "Öncelikli Görev Eklendi",
                        "Yeni görev eklendi: " + task.getName()
                );
                logger.info("Öncelikli görev için e-posta gönderildi: {}", task.getName());
            } catch (Exception e) {
                logger.error("E-posta gönderimi sırasında hata oluştu: {}", e.getMessage(), e);
            }
        }

        return savedTask;
    }

    // Tüm görevleri listele
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // ID’ye göre görev getir
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Görev sil
    public void deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
            logger.info("Görev silindi. ID: {}", id);
        } catch (Exception e) {
            logger.error("Görev silme sırasında hata oluştu. ID: {}", id, e);
            throw e;
        }
    }

    // Görev güncelle
    public Task updateTask(Task task) {
        Task updatedTask = taskRepository.save(task);

        if (updatedTask.isCompleted()) {
            logTaskCompletion(updatedTask); // log.txt'ye yaz
        }

        return updatedTask;
    }

    // Görev tamamlandığında log dosyasına yaz
    private void logTaskCompletion(Task task) {
        String logFilePath = "log.txt";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = "[" + timestamp + "] Görev tamamlandı: \"" + task.getName() + "\"";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logEntry);
            writer.newLine();
            logger.info("Tamamlanan görev loglandı: {}", task.getName());
        } catch (IOException e) {
            logger.error("Log dosyasına yazılırken hata oluştu: {}", e.getMessage(), e);
        }
    }

    // Öncelikli görevler için e-posta hatırlatması
    public void sendPriorityTaskReminder(Task task, String email) {
        if (task.getPriority() >= 4 && !task.isCompleted()) {
            String subject = "Öncelikli Görev Hatırlatması";
            String text = "Görev: " + task.getName() + " tamamlanmamış. Lütfen kontrol edin.";
            try {
                mailService.sendSimpleMail(email, subject, text);
                logger.info("Hatırlatma e-postası gönderildi: {}", email);
            } catch (Exception e) {
                logger.error("Hatırlatma e-postası gönderilemedi: {}", e.getMessage(), e);
            }
        }
    }
}
