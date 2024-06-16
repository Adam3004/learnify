package com.brightpath.learnify.persistance.quiz;

import com.brightpath.learnify.model.QuizSimpleResultDto;

public record QuizSimpleResult(int incorrect,
                               int correct) {
    public QuizSimpleResultDto convertToQuizSimpleResultDto() {
        return new QuizSimpleResultDto(incorrect, correct);
    }
}