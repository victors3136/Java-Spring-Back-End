package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
            select p_name
            from sdi_permission
            inner join sdi_role_permission on p_id = rp_permission
            inner join sdi_role on rp_role = r_id
            inner join sdi_user on u_role = r_id
            where u_id = :id
            """, nativeQuery = true)
    List<String> findPermissionsByUserId(@Param("id") UUID id);

    @Query(value = """
            -- noinspection SqlResolve
            select u_id
            from sdi_permission
            inner join sdi_role_permission on p_id = rp_permission
            inner join sdi_role on rp_role = r_id
            inner join sdi_user on u_role = r_id
            where p_name = :name
            """, nativeQuery = true)
    List<UUID> findByPermission(@Param("name") String name);

    @Query(value = """
            -- noinspection SqlResolve
            select r_id
            from sdi_role
            where r_name = :roleName
            """, nativeQuery = true)
    Optional<UUID> findRoleByName(@Param("roleName") String roleName);
}
