package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = """
            -- noinspection SqlResolve
            select *
            from sdi_user
            where u_username = :username
            """, nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = """
            -- noinspection SqlResolve
            select *
            from sdi_user
            where u_email = :email
            """, nativeQuery = true)
    Collection<User> findByEmail(@Param("email") String email);

    @Query(value = """
            -- noinspection SqlResolve
            update sdi_user
            set u_username = :name,
                u_email = :email,
                u_password = :password
            where u_id = :id
            """, nativeQuery = true)
    void updateUserById(@Param("id") UUID id, @Param("name") String name, @Param("email") String email, @Param("password") String password);

    @Query(value = """
            -- noinspection SqlResolve
            select (
                exists (
                    select 1
                    from sdi_user
                    where u_username = :username ))
            """, nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);


    @Query(value = """
            -- noinspection SqlResolve
            select (
                exists (
                    select 1
                    from sdi_user
                    where u_email = :email ))
            """, nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
