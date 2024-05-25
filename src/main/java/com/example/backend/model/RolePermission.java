package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sdi_role_permission")
public class RolePermission {
    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "rp_role")
    private Role role;

    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "rp_permission", referencedColumnName = "p_id")
    private Permission permission;

}