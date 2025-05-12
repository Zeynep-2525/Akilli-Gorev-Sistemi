package com.odev.task;

import java.util.Scanner;
import org.springframework.stereotype.Service;

@Service
public class TaskInputService {

    private final Scanner scanner = new Scanner(System.in);

    public Task promptUserForTask() {
        System.out.println("Yeni bir görev ekleyelim!");

        System.out.print("Görev başlığı: ");
        String title = scanner.nextLine();

        System.out.print("Açıklama: ");
        String description = scanner.nextLine();

        System.out.print("Öncelik (LOW, MEDIUM, HIGH): ");
        String priorityInput = scanner.nextLine().toUpperCase();

        TaskPriority priority;
        try {
            priority = TaskPriority.valueOf(priorityInput);
        } catch (IllegalArgumentException e) {
            System.out.println("❗ Geçersiz öncelik girildi. Varsayılan olarak LOW seçildi.");
            priority = TaskPriority.LOW;
        }

        // Görev oluştur
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);

        return task;
    }
}
