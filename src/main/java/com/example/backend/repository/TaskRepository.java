package com.example.backend.repository;

import com.example.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    @Query(value = """
            select count(*)
            from sdi_task inner join sdi_subtask
                    on t_id = s_task
                where t_id = :id
            """,
            nativeQuery = true)
    long countSubtasksByTask(@org.springframework.data.repository.query.Param("id") UUID id);

    @Query(value = """
            select *
            from sdi_task
                where t_user = :id
            """,
            nativeQuery = true)
    List<Task> findByUserId(@org.springframework.data.repository.query.Param("id") UUID userId);
}
