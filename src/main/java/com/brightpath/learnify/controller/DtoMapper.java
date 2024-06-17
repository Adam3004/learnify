package com.brightpath.learnify.controller;

import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.model.NoteSummaryDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import com.brightpath.learnify.model.UserSummaryDto;
import com.brightpath.learnify.model.WorkspaceSummaryDto;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    public NoteSummaryDto asNoteSummaryDto(Note note) {
        return new NoteSummaryDto()
                .id(note.uuid().toString())
                .title(note.title())
                .description(note.description())
                .workspace(asWorkspaceSummaryDto(note.workspace()))
                .author(asUserSummaryDto(note.owner()))
                .createdAt(note.createdAt())
                .updatedAt(note.updatedAt());
    }

    public UserSummaryDto asUserSummaryDto(User owner) {
        return new UserSummaryDto()
                .id(owner.id().toString())
                .displayName(owner.displayName());
    }

    public WorkspaceSummaryDto asWorkspaceSummaryDto(Workspace workspace) {
        return new WorkspaceSummaryDto()
                .id(workspace.id().toString())
                .displayName(workspace.displayName());
    }

    public QuizSummaryDto asQuizSummaryDto(Quiz quiz) {
        return quiz.convertToQuizSummaryDto();
    }
}
