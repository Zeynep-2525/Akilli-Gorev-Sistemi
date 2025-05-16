package com.odev.cli;

import com.odev.model.Task;
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
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SMART TASK MANAGER ===");
            System.out.println("1. Görevleri Listele");
            System.out.println("2. Yeni Görev Ekle");
            System.out.println("3. Görev Güncelle");
            System.out.println("4. Görev Sil");
            System.out.println("5. Çıkış");
            System.out.print("Seçim: ");

            int secim;
            try {
                secim = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Lütfen geçerli bir sayı girin.");
                continue;
            }

            switch (secim) {
                case 1:
                    List<Task> tasks = taskService.getAllTasks();
                    if (tasks.isEmpty()) {
                        System.out.println("Henüz görev bulunmamaktadır.");
                    } else {
                        tasks.forEach(System.out::println);
                    }
                    break;

                case 2:
                    System.out.print("Başlık: ");
                    String title = scanner.nextLine();
                    System.out.print("Açıklama: ");
                    String description = scanner.nextLine();
                    System.out.print("Öncelik (LOW, MEDIUM, HIGH): ");
                    String priority = scanner.nextLine();
                    System.out.print("Bitiş tarihi (yyyy-MM-dd): ");
                    String tarihGiris = scanner.nextLine();

                    LocalDateTime deadline;
                    try {
                        deadline = LocalDate.parse(tarihGiris).atStartOfDay();
                    } catch (Exception e) {
                        System.out.println("Tarih formatı geçersiz. Görev eklenemedi.");
                        break;
                    }

                    Task newTask = new Task(title, description, priority, "java.tester.odev@gmail.com", deadline);
                    taskService.createTask(newTask);
                    System.out.println("✅ Görev başarıyla eklendi.");
                    break;

                case 3:
                    System.out.print("Güncellenecek görev ID: ");
                    Long updateId = Long.parseLong(scanner.nextLine());

                    System.out.print("Yeni başlık: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Yeni açıklama: ");
                    String newDesc = scanner.nextLine();
                    System.out.print("Yeni öncelik: ");
                    String newPriority = scanner.nextLine();
                    System.out.print("Yeni bitiş tarihi (yyyy-MM-dd): ");
                    LocalDateTime newDeadline;
                    try {
                        newDeadline = LocalDate.parse(scanner.nextLine()).atStartOfDay();
                    } catch (Exception e) {
                        System.out.println("Tarih formatı hatalı. Güncelleme başarısız.");
                        break;
                    }

                    Task updatedTask = new Task(newTitle, newDesc, newPriority, "java.tester.odev@gmail.com", newDeadline);
                    boolean updated = taskService.updateTask(updateId, updatedTask);
                    System.out.println(updated ? "✅ Görev güncellendi." : "⚠️ Görev bulunamadı.");
                    break;

                case 4:
                    System.out.print("Silinecek görev ID: ");
                    Long deleteId = Long.parseLong(scanner.nextLine());
                    boolean deleted = taskService.deleteTask(deleteId);
                    System.out.println(deleted ? "🗑️ Görev silindi." : "⚠️ Görev bulunamadı.");
                    break;

                case 5:
                    System.out.println("Programdan çıkılıyor...");
                    return;

                default:
                    System.out.println("Geçersiz seçim yaptınız. Lütfen tekrar deneyin.");
                    break;
            }
        }
    }
}

