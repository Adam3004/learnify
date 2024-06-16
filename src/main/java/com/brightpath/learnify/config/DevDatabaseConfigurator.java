package com.brightpath.learnify.config;

import com.brightpath.learnify.domain.auth.AuthorizationService;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.domain.workspace.WorkspaceService;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevDatabaseConfigurator {

    @Bean
    CommandLineRunner commandLineRunner(
            WorkspaceService workspaceService,
            NoteService noteService,
            AuthorizationService authorizationService
    ) {
        return args -> {
            User user = authorizationService.defaultUser();
            Workspace workspace1 = workspaceService.createWorkspace("Semestr 1");
            workspaceService.createWorkspace("Semestr 2");
            workspaceService.createWorkspace("Semestr 3");
            workspaceService.createWorkspace("Semestr 4");
            noteService.createNote(
                    "Test note",
                    "This is a test note",
                    workspace1.id(),
                    user.uuid()
            );
            System.out.println("Database initialized");
        };
    }
}
