package com.odev.cli;

import com.odev.taskmanager.model.Task;
import com.odev.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Component
public class TaskManagerCLI implements CommandLineRunner {

    @Autowired
    private TaskService taskService;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== SMART TASK MANAGER ===");
            System.out.println("1. Görevleri Listele");
            System.out.println("2. Yeni Görev Ekle");
            System.out.println("3. Görev Güncelle");
            System.out.println("4. Görev Sil");
            System.out.println("5. Çıkış");
            System.out.print("Seçim: ");
            int secim = scanner.nextInt();
            scanner.nextLine(); // \n temizle

            if (secim == 1) {
                List<Task> tasks = taskService.getAllTasks();
                tasks.forEach(System.out::println);
            } else if (secim == 2) {
                System.out.print("Başlık: ");
                String title = scanner.nextLine();
                System.out.print("Açıklama: ");
                String desc = scanner.nextLine();
                System.out.print("Öncelik (LOW, MEDIUM, HIGH): ");
                String priority = scanner.nextLine();
                System.out.print("Bitiş tarihi (yyyy-mm-dd): ");
                LocalDate date = LocalDate.parse(scanner.nextLine());
                LocalDateTime dateTime = date.atStartOfDay();

                Task task = new Task(title, desc, priority, "java.tester.odev@gmail.com", dateTime);
                taskService.createTask(task);
                System.out.println("Görev eklendi.");
            } else if (secim == 3) {
                System.out.print("Güncellenecek görev ID: ");
                Long id = scanner.nextLong();
                scanner.nextLine(); // temizle
                System.out.print("Yeni başlık: ");
                String title = scanner.nextLine();
                System.out.print("Yeni açıklama: ");
                String desc = scanner.nextLine();
                System.out.print("Yeni öncelik: ");
                String priority = scanner.nextLine();
                System.out.print("Yeni tarih: ");
                LocalDate date = LocalDate.parse(scanner.nextLine());
                LocalDateTime dateTime = date.atStartOfDay();

                Task updatedTask = new Task(title, desc, priority, "java.tester.odev@gmail.com", dateTime);
                taskService.updateTask(id, updatedTask);
                System.out.println(" Güncellendi.");
            } else if (secim == 4) {
                System.out.print("Silinecek görev ID: ");
                Long id = scanner.nextLong();
                scanner.nextLine(); // temizle
                boolean result = taskService.deleteTask(id);
                System.out.println(result ? " Silindi." : "Görev bulunamadı.");
            } else if (secim == 5) {
                System.out.println("Çıkılıyor...");
                break;
            } else {
                System.out.println("Geçersiz seçim!");
            }
        }
    }
}
