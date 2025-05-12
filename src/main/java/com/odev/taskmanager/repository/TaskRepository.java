package com.odev.taskmanager.repository;

import com.odev.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByPriority(String priority); // HIGH, MEDIUM, LOW gibi
}

