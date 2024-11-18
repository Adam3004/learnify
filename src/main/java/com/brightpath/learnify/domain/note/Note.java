package com.brightpath.learnify.domain.note;

import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Note(
        UUID id,
        String title,
        String description,
        Workspace workspace,
        User owner,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        NoteType type,
        int pagesCount
) {
}
