package com.brightpath.learnify.domain.quiz.result;

import java.time.OffsetDateTime;

public record QuizSimpleResult(int incorrect,
                               int correct,
                               OffsetDateTime tryDate) {

    public boolean isGreaterThan(Integer correctToCompare, Integer incorrectToCompare) {
        if (correctToCompare == null || incorrectToCompare == null) {
            return true;
        }
        return (correct / (double) incorrect) > (correctToCompare / (double) incorrectToCompare);
    }
}