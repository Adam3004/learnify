package com.brightpath.learnify.domain.workspace;

import com.brightpath.learnify.model.WorkspaceSummaryDto;

import java.util.UUID;

public record Workspace(
        UUID id,
        String displayName
){
    public WorkspaceSummaryDto toDto() {
        return new WorkspaceSummaryDto(id, displayName);
    }
}
