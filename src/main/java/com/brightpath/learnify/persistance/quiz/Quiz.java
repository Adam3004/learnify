package com.brightpath.learnify.persistance.quiz;

import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import com.brightpath.learnify.persistance.common.User;
import com.brightpath.learnify.persistance.common.Workspace;
import lombok.Builder;

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
                   ZonedDateTime lastTryDate) {

    public QuizDetailsDto convertToQuizDetailsDto() {
        QuizDetailsDto quizDetailsDto = new QuizDetailsDto(id.toString(), workspace.id().toString(), title, description, numberOfQuestions, author.convertToUserSummaryDto());
        quizDetailsDto.setBestScore(bestScore == null ? null : bestScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastScore(lastScore == null ? null : lastScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastTryDate(lastTryDate == null ? null : lastTryDate.toOffsetDateTime());
        return quizDetailsDto;
    }

    public QuizSummaryDto convertToQuizSummaryDto() {
        return new QuizSummaryDto(id.toString(), workspace.id().toString(), title, findScore());
    }

    private String findScore() {
        if (numberOfQuestions == 0 || lastScore == null) {
            return "-1";
        }
        double score = lastScore.correct() / (double) numberOfQuestions;
        return score + "%";
    }
}
