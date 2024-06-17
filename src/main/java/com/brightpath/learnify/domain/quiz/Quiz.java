package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
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
        QuizDetailsDto quizDetailsDto = new QuizDetailsDto(id.toString(), workspace.toDto(), title, description, numberOfQuestions, author.convertToUserSummaryDto(), createdAt);
        quizDetailsDto.setBestScore(bestScore == null ? null : bestScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastScore(lastScore == null ? null : lastScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastTryDate(lastTryDate);
        return quizDetailsDto;
    }

    public QuizSummaryDto convertToQuizSummaryDto() {
        return new QuizSummaryDto(id.toString(), workspace.toDto(), title, findScore(), author.convertToUserSummaryDto());
    }

    private String findScore() {
        if (numberOfQuestions == 0 || lastScore == null) {
            return "-1";
        }
        double score = lastScore.correct() / (double) numberOfQuestions;
        return score + "%";
    }
}
