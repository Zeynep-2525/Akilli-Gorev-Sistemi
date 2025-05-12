package com.odev.taskmanager.controller;

import com.odev.taskmanager.model.Task;
import com.odev.taskmanager.service.TaskService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-task")
public class TestTaskController {    //TEST İÇİN YAZDIM DÜZELTİLECEK-Zeynep

    private final TaskService taskService;

    public TestTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTestTask() {
        Task task = new Task();
        task.setTitle("Test Görevi");
        task.setDescription("Bu sadece test için bir görevdir.");
        task.setPriority("HIGH");
        task.setUserEmail("java.tester.odev@gmail.com"); // Mailtrap'teki alıcı adresiyle eşleşmeli!

        return taskService.saveTask(task);
    }
}
