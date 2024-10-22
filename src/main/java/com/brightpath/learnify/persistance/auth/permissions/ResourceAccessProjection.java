package com.brightpath.learnify.persistance.auth.permissions;

import com.brightpath.learnify.domain.auth.permission.Access;
import com.brightpath.learnify.domain.common.ResourceType;

import java.util.UUID;

public record ResourceAccessProjection(
        UUID resourceId,
        ResourceType resourceType,
        Access access
) {
}
