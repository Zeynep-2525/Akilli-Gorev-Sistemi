package com.odev.taskmanager.service.impl;

import com.odev.taskmanager.model.Task;
import com.odev.taskmanager.repository.TaskRepository;
import com.odev.taskmanager.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getHighPriorityTasks() {
        return taskRepository.findByPriority("HIGH"); // ya da senin belirlediğin kritere göre
    }
}
