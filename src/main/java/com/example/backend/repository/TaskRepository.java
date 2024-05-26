package com.example.backend.repository;

import com.example.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    @Query(value = """
            -- noinspection SqlResolve
            select *
            from sdi_task
                where t_user = :id
            """,
            nativeQuery = true)
    List<Task> findByUserId(@Param("id") UUID id);

    @Query(value = """
            -- noinspection SqlResolve
            update sdi_task
            set t_name = :name,
                t_description = :description,
                t_priority = :priority,
                t_due_date = :due_date,
                t_user = :user
            where t_id = :id;
            """, nativeQuery = true)
    void updateTaskById(@Param("id") UUID id,
                        @Param("name") String name,
                        @Param("description") String description,
                        @Param("priority") int priority,
                        @Param("due_date") Instant dueDate,
                        @Param("user") UUID user);
}
