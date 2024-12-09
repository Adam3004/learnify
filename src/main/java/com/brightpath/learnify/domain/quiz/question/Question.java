package com.brightpath.learnify.domain.quiz.question;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record Question(
        UUID id,
        String question,
        QuestionType type,
        UUID quizId,
        Integer weight,
        String choices,
        String feedback,
        String otherProperties
) {
}
