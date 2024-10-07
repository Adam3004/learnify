package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record Quiz(UUID id,
                   Workspace workspace,
                   String title,
                   String description,
                   int numberOfQuestions,
                   QuizSimpleResult lastScore,
                   QuizSimpleResult bestScore,
                   User author,
                   OffsetDateTime lastTryDate,
                   OffsetDateTime createdAt) {

    public QuizDetailsDto convertToQuizDetailsDto() {
        QuizDetailsDto quizDetailsDto = new QuizDetailsDto(id, workspace.toDto(), title, description, numberOfQuestions, author.convertToUserSummaryDto(), createdAt);
        quizDetailsDto.setBestScore(bestScore == null ? null : bestScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastScore(lastScore == null ? null : lastScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastTryDate(lastTryDate);
        return quizDetailsDto;
    }

    public QuizSummaryDto convertToQuizSummaryDto() {
        return new QuizSummaryDto(id, workspace.toDto(), title, findScore(), author.convertToUserSummaryDto());
    }

    private String findScore() {
        if (numberOfQuestions == 0 || lastScore == null) {
            return "-1";
        }
        double score = lastScore.correct() / (double) numberOfQuestions;
        return score + "%";
    }
}
