package com.example.backend.repository;

import com.example.backend.model.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
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

}
