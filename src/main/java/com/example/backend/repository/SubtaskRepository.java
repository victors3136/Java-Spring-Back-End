package com.example.backend.repository;

import com.example.backend.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubtaskRepository extends JpaRepository<Subtask, UUID> {
    List<Subtask> findByTask(UUID id);
}
