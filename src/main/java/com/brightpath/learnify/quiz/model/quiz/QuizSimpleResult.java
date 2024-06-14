package com.brightpath.learnify.quiz.model.quiz;

import com.brightpath.learnify.model.QuizSimpleResultDto;

public record QuizSimpleResult(int incorrect,
                               int correct) {
    public QuizSimpleResult(QuizSimpleResultDto quizSimpleResultDto) {
        this(quizSimpleResultDto.getCorrect(),
                quizSimpleResultDto.getIncorrect());
    }
}
