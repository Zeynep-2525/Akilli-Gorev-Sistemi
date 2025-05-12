package com.odev.taskmanager.service;

import com.odev.taskmanager.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> getHighPriorityTasks();
    Task saveTask(Task task);

}
