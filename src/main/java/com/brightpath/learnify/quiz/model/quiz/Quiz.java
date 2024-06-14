package com.brightpath.learnify.quiz.model.quiz;

import com.brightpath.learnify.model.QuizDto;

public record Quiz(String id,
                   String workspaceId,
                   String title,
                   String score,
                   String author,
                   String date) {
    public Quiz(QuizDto quizDto) {
        this(
                quizDto.getId(),
                quizDto.getWorkspaceId(),
                quizDto.getTitle(),
                quizDto.getScore(),
                quizDto.getAuthor(),
                quizDto.getDate()
        );
    }
}
