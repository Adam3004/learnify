package com.brightpath.learnify.domain.binding;

import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;

import java.util.UUID;

public record Binding(
        UUID id,
        UUID noteId,
        UUID quizId
) {
}
