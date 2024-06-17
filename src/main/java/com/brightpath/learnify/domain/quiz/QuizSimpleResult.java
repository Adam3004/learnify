package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.model.QuizSimpleResultDto;

public record QuizSimpleResult(int incorrect,
                               int correct) {
    public QuizSimpleResultDto convertToQuizSimpleResultDto() {
        return new QuizSimpleResultDto(incorrect, correct);
    }

    public boolean isGreaterThan(Integer correctToCompare, Integer incorrectToCompare) {
        if (correctToCompare == null || incorrectToCompare == null) {
            return true;
        }
        return (correct / (double) incorrect) > (correctToCompare / (double) incorrectToCompare);
    }
}