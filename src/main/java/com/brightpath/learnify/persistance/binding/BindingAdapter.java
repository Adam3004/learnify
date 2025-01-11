package com.brightpath.learnify.persistance.binding;

import com.brightpath.learnify.domain.binding.Binding;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface BindingAdapter {
    Binding createBinding(UUID noteId, UUID quizId);

    List<Note> listNotesBoundToQuiz(UUID quizId, String userId);

    List<Quiz> listQuizzesBoundToNote(UUID noteId, String userId);

    @Transactional
    void removeBindingForQuiz(UUID quizId);

    @Transactional
    void removeBindingForNote(UUID noteId);
}
