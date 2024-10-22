package com.brightpath.learnify.persistance.auth.permissions;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.common.ResourceType;
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

    @OneToMany
    @JoinColumn(name = "permissions")
    private Set<PermissionEntity> permissions;

    @Column(name = "resource_type", nullable = false)
    private ResourceType resourceType;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;
}
