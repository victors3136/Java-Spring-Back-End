package com.example.backend.repository;

import com.example.backend.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface SubtaskRepository extends JpaRepository<Subtask, UUID>, JpaSpecificationExecutor<Subtask> {

    Collection<Subtask> findByTask(UUID sTask);

    @Query(value = """
            -- noinspection SqlResolve
            select count(*)
            from sdi_subtask
            where s_task = :id
            """,
            nativeQuery = true)
    long countSubtasksByTask(@Param("id") UUID id);

    @Query(value = """
            -- noinspection SqlResolve
            update sdi_subtask
            set s_subject = :subject,
                s_task = :task
            where s_id = :id;
            """, nativeQuery = true)
    void updateSubtaskById(@Param("id") UUID id,
                           @Param("subject") String subject,
                           @Param("task") UUID task);
}
