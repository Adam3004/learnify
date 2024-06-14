package com.brightpath.learnify.quiz.model.quiz.question;

import com.brightpath.learnify.model.QuestionDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public abstract class Question {
    protected final String question;
    protected final QuestionType type;
    protected final String quizId;
    protected final Integer weight;
    protected final List<String> choices;
    protected final List<String> feedback;


    public Question(String question, QuestionDto.TypeEnum type, String quizId, Integer weight, List<String> choices, List<String> feedback) {
        this.question = question;
        this.type = QuestionType.convert(type);
        this.quizId = quizId;
        this.weight = weight;
        this.choices = choices;
        this.feedback = feedback;
    }

    public abstract <T> int getAnswer();

    public enum QuestionType {
        MULTIPLE_CHOICE("multiple-choice"),

        SINGLE_CHOICE("single-choice");

        private final String value;

        QuestionType(String value) {
            this.value = value;
        }

        public static QuestionType convert(QuestionDto.TypeEnum typeEnum) {
            if (typeEnum.equals(QuestionDto.TypeEnum.MULTIPLE_CHOICE)) {
                return MULTIPLE_CHOICE;
            } else {
                return SINGLE_CHOICE;
            }
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
}
