package com.brightpath.learnify.persistance.note;

import com.brightpath.learnify.persistance.common.User;
import com.brightpath.learnify.persistance.common.Workspace;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Note(
        UUID uuid,
        String title,
        String description,
        Workspace workspace,
        User owner,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
