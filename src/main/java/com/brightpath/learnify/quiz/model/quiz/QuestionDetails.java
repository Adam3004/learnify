package com.brightpath.learnify.quiz.model.quiz;

import com.brightpath.learnify.model.QuizDetailsDto;

public record QuestionDetails(String id,
                              String name,
                              String description,
                              Integer numberOfQuestions,
                              QuizSimpleResult lastScore) {
    public QuestionDetails(QuizDetailsDto quizDetailsDto) {
        this(
                quizDetailsDto.getId(),
                quizDetailsDto.getName(),
                quizDetailsDto.getDescription(),
                quizDetailsDto.getNumberOfQuestions(),
                new QuizSimpleResult(quizDetailsDto.getLastScore())
        );
    }
}
