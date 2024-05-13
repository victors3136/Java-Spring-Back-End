package com.example.backend.repository;

import com.example.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    @Query(value = "SELECT COUNT(*) FROM task t INNER JOIN subtask s ON t.id = s.task WHERE t.id = :id",
            nativeQuery = true)
    long countSubtasksByTask(@org.springframework.data.repository.query.Param("id") UUID id);
}
