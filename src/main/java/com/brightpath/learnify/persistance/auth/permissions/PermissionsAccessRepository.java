package com.brightpath.learnify.persistance.auth.permissions;

import com.brightpath.learnify.domain.common.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionsAccessRepository extends JpaRepository<PermissionsAccessEntity, String> {

    @Query("SELECT new com.brightpath.learnify.persistance.auth.permissions.SimplePermissionsAccessProjection(p.id, p.permissionLevel, p.resourceType, p.resourceId) " +
            "FROM PermissionsAccessEntity p WHERE p.permissionLevel = com.brightpath.learnify.domain.auth.permission.PermissionLevel.PUBLIC AND (:resourceType IS NULL OR p.resourceType = :resourceType)")
    Page<SimplePermissionsAccessProjection> findAllPublicResources(Pageable pageable, ResourceType resourceType);

    @Query("SELECT new com.brightpath.learnify.persistance.auth.permissions.ResourceAccessProjection(p.resourceId, p.resourceType, a.access) " +
            "FROM PermissionsAccessEntity p LEFT JOIN PermissionEntity a WHERE p.permissionLevel = com.brightpath.learnify.domain.auth.permission.PermissionLevel.PUBLIC OR a.userId = :userId")
    Page<ResourceAccessProjection> findAllPublicResourcesWithUserAccess(Pageable pageable, UUID userId);

    @Query("SELECT new com.brightpath.learnify.persistance.auth.permissions.ResourceAccessProjection(p.resourceId, p.resourceType, a.access) " +
            "FROM PermissionsAccessEntity p JOIN PermissionEntity a WHERE p.id = :permissionAccessId AND a.userId = :userId")
    Optional<ResourceAccessProjection> findUserAccessToResource(UUID userId, String permissionAccessId);
}
