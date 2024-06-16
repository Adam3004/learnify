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
//    public Quiz(QuizDetailsDto quizDto) {
//        this(
//                quizDto.getId(),
//                quizDto.getWorkspaceId(),
//                quizDto.getTitle(),
//                quizDto.getDescription(),
//                quizDto.getNumberOfQuestions(),
//                new QuizSimpleResult(quizDto.getLastScore()),
//                new QuizSimpleResult(quizDto.getBestScore()),
//                new User(quizDto.getAuthor()),
//                ZonedDateTime.from(quizDto.getLastTryDate())
//        );
//    }

//    public Quiz(QuizCreationDto quizCreationDto) {
//        this(
//                null,
//                null,
//                quizCreationDto.getTitle(),
//                quizCreationDto.getDescription(),
//                quizCreationDto.getNumberOfQuestions(),
//                null,
//                null,
//                null,
//                null
//        );
//    }

    public QuizDetailsDto convertToQuizDetailsDto() {
        QuizDetailsDto quizDetailsDto = new QuizDetailsDto(id.toString(), workspace.id().toString(), title, description, numberOfQuestions, author.convertToUserSummaryDto());
        quizDetailsDto.setBestScore(bestScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastScore(lastScore.convertToQuizSimpleResultDto());
        quizDetailsDto.setLastTryDate(lastTryDate.toOffsetDateTime());
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
