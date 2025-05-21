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

    public Task addTask(Task task) {
        Task savedTask = taskRepository.save(task);

        if ("HIGH".equalsIgnoreCase(task.getPriority())) {
            try {
                mailService.sendSimpleMail(
                        task.getUserEmail(),
                        "Öncelikli Görev Eklendi",
                        "Yeni görev eklendi: " + task.getTitle()
                );
                logger.info("Öncelikli görev için e-posta gönderildi: {}", task.getTitle());
            } catch (Exception e) {
                logger.error("E-posta gönderimi sırasında hata oluştu: {}", e.getMessage(), e);
            }
        }

        return savedTask;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public void deleteTask(Long id) {
        try {
            taskRepository.deleteById(id);
            logger.info("Görev silindi. ID: {}", id);
        } catch (Exception e) {
            logger.error("Görev silme sırasında hata oluştu. ID: {}", id, e);
            throw e;
        }
    }

    public Task updateTask(Task task) {
        Task updatedTask = taskRepository.save(task);
        if (updatedTask.isCompleted()) {
            logTaskCompletion(updatedTask);
        }
        return updatedTask;
    }

    //  CLI için kullanılacak doğru güncelleme metodu
    public boolean updateTask(Long id, Task updatedTask) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isPresent()) {
            Task existingTask = optionalTask.get();
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setDueDate(updatedTask.getDueDate());
            existingTask.setCompleted(updatedTask.isCompleted());
            existingTask.setUserEmail(updatedTask.getUserEmail());

            taskRepository.save(existingTask);

            if (existingTask.isCompleted()) {
                logTaskCompletion(existingTask);
            }

            return true;
        }

        return false;
    }

    private void logTaskCompletion(Task task) {
        String logFilePath = "log.txt";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = "[" + timestamp + "] Görev tamamlandı: \"" + task.getTitle() + "\"";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write(logEntry);
            writer.newLine();
            logger.info("Tamamlanan görev loglandı: {}", task.getTitle());
        } catch (IOException e) {
            logger.error("Log dosyasına yazılırken hata oluştu: {}", e.getMessage(), e);
        }
    }

    public void sendPriorityTaskReminder(Task task, String email) {
        if ("HIGH".equalsIgnoreCase(task.getPriority()) && !task.isCompleted()) {
            String subject = "Öncelikli Görev Hatırlatması";
            String text = "Görev: " + task.getTitle() + " tamamlanmamış. Lütfen kontrol edin.";
            try {
                mailService.sendSimpleMail(email, subject, text);
                logger.info("Hatırlatma e-postası gönderildi: {}", email);
            } catch (Exception e) {
                logger.error("Hatırlatma e-postası gönderilemedi: {}", e.getMessage(), e);
            }
        }
    }

    public List<Task> getHighPriorityTasks() {
        return taskRepository.findByPriorityGreaterThanEqualAndCompletedFalse(TaskPriority.HIGH);
    }

}
