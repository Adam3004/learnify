package com.brightpath.learnify.persistance.auth.permissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {

}
