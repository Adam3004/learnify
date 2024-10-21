package com.brightpath.learnify.domain.quiz;

public record QuizSimpleResult(int incorrect,
                               int correct) {

    public boolean isGreaterThan(Integer correctToCompare, Integer incorrectToCompare) {
        if (correctToCompare == null || incorrectToCompare == null) {
            return true;
        }
        return (correct / (double) incorrect) > (correctToCompare / (double) incorrectToCompare);
    }
}