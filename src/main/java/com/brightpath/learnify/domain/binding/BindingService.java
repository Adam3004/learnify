package com.brightpath.learnify.domain.binding;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.domain.binding.port.BindingPersistencePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.common.ResourceType.NOTE;
import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@Service
public class BindingService {
    private final NoteService noteService;
    private final QuizService quizService;
    private final PermissionAccessService permissionAccessService;
    private final BindingPersistencePort bindingPersistencePort;

    @Autowired
    public BindingService(@Lazy NoteService noteService, @Lazy QuizService quizService, PermissionAccessService permissionAccessService, BindingPersistencePort bindingPersistencePort) {
        this.noteService = noteService;
        this.quizService = quizService;
        this.permissionAccessService = permissionAccessService;
        this.bindingPersistencePort = bindingPersistencePort;
    }

    public Binding createBinding(UUID noteId, UUID quizId) {
        return bindingPersistencePort.createBinding(noteId, quizId);
    }

    public List<Note> listNotesBoundToQuiz(UUID quizId, String userId) {
        checkIfQuizExists(quizId, userId);
        List<Note> notes = bindingPersistencePort.listNotesBoundToQuiz(quizId, userId);
        return notes.stream()
                .filter(note -> permissionAccessService.checkUserPermissionToViewResource(note.id(), NOTE))
                .toList();
    }

    public List<Quiz> listQuizzesBoundToNote(UUID noteId, String userId) {
        checkIfNoteExists(noteId);
        List<Quiz> quizzes = bindingPersistencePort.listQuizzesBoundToNote(noteId, userId);
        return quizzes.stream()
                .filter(quiz -> permissionAccessService.checkUserPermissionToViewResource(quiz.id(), QUIZ))
                .toList();
    }

    @Transactional
    public void removeBindingForQuiz(UUID quizId) {
        bindingPersistencePort.removeBindingForQuiz(quizId);
    }

    @Transactional
    public void removeBindingForNote(UUID noteId) {
        bindingPersistencePort.removeBindingForNote(noteId);
    }

    private void checkIfNoteExists(UUID noteId) {
        noteService.checkIfNoteExists(noteId);
    }

    private void checkIfQuizExists(UUID quizId, String userId) {
        quizService.showQuizById(quizId, userId);
    }
}
