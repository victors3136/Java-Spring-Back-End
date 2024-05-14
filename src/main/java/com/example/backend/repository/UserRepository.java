package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = """
            select *
            from sdi_user
            where u_username = :username
            """, nativeQuery = true)
    Optional<User> findByUsername(@org.springframework.data.repository.query.Param("username")String username);
}
