package com.brightpath.learnify.persistance.auth.permissions;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.common.ResourceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "permissions_access")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PermissionsAccessEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "permission_level", nullable = false)
    private PermissionLevel permissionLevel;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "permissions_access_id") // This column will be added to PermissionEntity table
    private Set<PermissionEntity> permissions = new HashSet<>();

    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    public void addPermission(PermissionEntity permission) {
        permissions.add(permission);
    }

    public void removePermissionForUser(String userId) {
        permissions.removeIf(p -> p.getUserId().equals(userId));
    }
}
