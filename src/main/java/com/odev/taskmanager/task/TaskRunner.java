package com.odev.taskmanager.task;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TaskRunner implements CommandLineRunner {

    private final TaskInputService taskInputService;
    private final TaskRepository taskRepository;

    public TaskRunner(TaskInputService taskInputService, TaskRepository taskRepository) {
        this.taskInputService = taskInputService;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Task newTask = taskInputService.promptUserForTask();
        taskRepository.save(newTask);
        System.out.println("Görev başarıyla kaydedildi: " + newTask.getTitle());
    }
}
