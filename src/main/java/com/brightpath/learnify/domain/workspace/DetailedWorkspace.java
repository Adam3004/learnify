package com.brightpath.learnify.domain.workspace;

import com.brightpath.learnify.domain.user.User;

import java.util.List;
import java.util.UUID;

public record DetailedWorkspace(
        UUID id,
        String displayName,
        User owner,
        Workspace parentWorkspace,
        List<Workspace> childWorkspaces
) { }
