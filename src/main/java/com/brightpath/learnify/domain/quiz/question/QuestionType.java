package com.brightpath.learnify.domain.quiz.question;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionType {
    MULTIPLE_CHOICE("multiple-choice"),

    SINGLE_CHOICE("single-choice");

    private final String value;

    QuestionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static QuestionType fromValue(String value) {
        for (QuestionType b : QuestionType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
