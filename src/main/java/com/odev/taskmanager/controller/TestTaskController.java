package com.odev.taskmanager.controller;

import com.odev.taskmanager.model.Task;
import com.odev.taskmanager.service.TaskService;
import com.odev.taskmanager.model.TaskPriority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-task")
public class TestTaskController {   

    private final TaskService taskService;

    public TestTaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task createTestTask() {
        Task task = new Task();
        task.setTitle("Test Görevi");
        task.setDescription("Bu sadece test için bir görevdir.");

        
        task.setPriority(TaskPriority.valueOf("HIGH"));

        task.setUserEmail("java.tester.odev@gmail.com"); 

        return taskService.saveTask(task);
    }
}
