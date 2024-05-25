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
            select *
            from sdi_user
            where u_username = :username
            """, nativeQuery = true)
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value = """
                select *
                from sdi_user
                where u_email = :email
            """, nativeQuery = true)
    Collection<User> findByEmail(@Param("email") String email);

    @Query(value = """
                update sdi_user
                set u_username = :name,
                    u_eail = :email,
                    u_password = :password
                where u_id = :id
            """, nativeQuery = true)
    void updateUserById(@Param("name") UUID id, @Param("name") String name, @Param("name") String email, @Param("name") String password);

    @Query(value = """
                select (
                    exists (
                        select 1
                        from sdi_user
                        where u_username = :username ))
            """, nativeQuery = true)
    boolean existsByUsername(@Param("username") String username);


    @Query(value = """
                select (
                    exists (
                        select 1
                        from sdi_user
                        where u_email = :email ))
            """, nativeQuery = true)
    boolean existsByEmail(@Param("email") String email);
}
