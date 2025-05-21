package com.smarttaskmanager.repository;

import com.smarttaskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Önceliği belirli bir seviyenin üstünde olan ve tamamlanmamış görevleri getir
	List<Task> findByPriorityGreaterThanEqualAndCompletedFalse(TaskPriority priority);

    // Belirli bir e-posta adresine ait görevleri getir
    List<Task> findByUserEmail(String email);

    // Tamamlanmış görevleri getir
    List<Task> findByCompletedTrue();

    // Tamamlanmamış görevleri getir
    List<Task> findByCompletedFalse();

    // Belirli bir önceliğe sahip görevleri getir
    List<Task> findByPriority(int priority);
}
