package com.example.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class RolePermissionId implements Serializable {
    @Column(name = "rp_role")
    private UUID roleId;

    @Column(name = "rp_permission")
    private UUID permissionId;

    public RolePermissionId() {
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public int hashCode() {
        return roleId.hashCode() + permissionId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RolePermissionId rpi
                && roleId.equals(rpi.roleId)
                && permissionId.equals(rpi.permissionId);
    }
}
