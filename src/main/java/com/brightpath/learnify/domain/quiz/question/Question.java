package com.brightpath.learnify.domain.quiz.question;

import com.brightpath.learnify.model.QuestionCreationDto;
import com.brightpath.learnify.model.QuestionDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.brightpath.learnify.domain.quiz.question.Question.QuestionType.convertToDto;

@Getter
@RequiredArgsConstructor
public class Question {
    private final UUID id;
    private final String question;
    private final QuestionType type;
    private final UUID quizId;
    private final Integer weight;
    private final String choices;
    private final String feedback;
    private final String otherProperties;

    public Question(QuestionCreationDto questionCreationDto, UUID quizId) {
        this.id = null;
        this.question = questionCreationDto.getQuestion();
        this.type = QuestionType.convertFromDto(questionCreationDto.getType());
        this.quizId = quizId;
        this.weight = questionCreationDto.getWeight();
        this.choices = questionCreationDto.getChoices();
        this.feedback = questionCreationDto.getFeedback();
        this.otherProperties = questionCreationDto.getOtherProperties();
    }

    public Question(QuestionDto questionDto, UUID quizId, UUID questionId) {
        this.id = questionId;
        this.question = questionDto.getQuestion();
        this.type = QuestionType.convertFromDto(questionDto.getType());
        this.quizId = quizId;
        this.weight = questionDto.getWeight();
        this.choices = questionDto.getChoices();
        this.feedback = questionDto.getFeedback();
        this.otherProperties = questionDto.getOtherProperties();
    }

    public QuestionDto convertToQuestionDto() {
        return new QuestionDto(id.toString(), question, convertToDto(type), quizId.toString(), weight, choices, feedback, otherProperties);
    }

    public enum QuestionType {
        MULTIPLE_CHOICE("multiple-choice"),

        SINGLE_CHOICE("single-choice");

        private final String value;

        QuestionType(String value) {
            this.value = value;
        }

        public static QuestionType convertFromDto(QuestionCreationDto.TypeEnum typeEnum) {
            if (typeEnum == QuestionCreationDto.TypeEnum.MULTIPLE_CHOICE) {
                return MULTIPLE_CHOICE;
            } else {
                return SINGLE_CHOICE;
            }
        }

        public static QuestionType convertFromDto(QuestionDto.TypeEnum typeEnum) {
            if (typeEnum == QuestionDto.TypeEnum.MULTIPLE_CHOICE) {
                return MULTIPLE_CHOICE;
            } else {
                return SINGLE_CHOICE;
            }
        }

        public static QuestionDto.TypeEnum convertToDto(QuestionType questionType) {
            if (questionType == MULTIPLE_CHOICE) {
                return QuestionDto.TypeEnum.MULTIPLE_CHOICE;
            } else {
                return QuestionDto.TypeEnum.SINGLE_CHOICE;
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
