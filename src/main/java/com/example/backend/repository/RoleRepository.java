package com.example.backend.repository;

import com.example.backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query(value = """
            -- noinspection SqlResolve
            select r_id
            from sdi_role
            where r_name = :name;
            """, nativeQuery = true)
    UUID getByName(@Param("name") String name);

}
