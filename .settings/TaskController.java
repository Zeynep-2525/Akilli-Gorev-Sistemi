package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Task addTask(@RequestBody Task task){
        return taskService.addTask(task);
    }

    @GetMapping
    public List<Task> getAllTasks(){
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        return taskService.getTaskById(id)
                .map(existingTask -> {
                    existingTask.setName(updatedTask.getName());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setCompleted(updatedTask.isCompleted());
                    Task savedTask = taskService.updateTask(existingTask);
                    return ResponseEntity.ok(savedTask);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
