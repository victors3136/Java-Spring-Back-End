package com.example.backend.repository;

import com.example.backend.model.Role;
import com.example.backend.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface SubtaskRepository extends JpaRepository<Subtask, UUID> {

    List<Subtask> findByTask(UUID sTask);
    @Query(value = """
            select count(*)
            from sdi_subtask
            where s_task = :id
            """,
            nativeQuery = true)
    long countSubtasksByTask(@org.springframework.data.repository.query.Param("id") UUID id);
}
