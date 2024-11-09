package com.brightpath.learnify.domain.binding;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.persistance.binding.BindingEntity;
import com.brightpath.learnify.persistance.binding.BindingRepository;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.common.ResourceType.NOTE;
import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@Service
@RequiredArgsConstructor
public class BindingService {

    @PersistenceContext
    private EntityManager entityManager;
    private final UuidProvider uuidProvider;
    private final BindingRepository bindingRepository;
    private final PersistentMapper persistentMapper;
    private final NoteService noteService;
    private final QuizService quizService;
    private final PermissionAccessService permissionAccessService;

    public Binding createBinding(UUID noteId, UUID quizId) {
        NoteEntity note = entityManager.getReference(NoteEntity.class, noteId);
        QuizEntity quiz = entityManager.getReference(QuizEntity.class, quizId);
        BindingEntity binding = new BindingEntity(uuidProvider.generateUuid(), note, quiz);
        BindingEntity save = bindingRepository.save(binding);
        return new Binding(save.getId(), save.getNote().getId(), save.getQuiz().getId());
    }

    public List<Note> listNotesBoundToQuiz(UUID quizId, String userId) {
        checkIfQuizExists(quizId, userId);
        List<NoteEntity> bindings = bindingRepository.findAllBoundNotesByQuizId(quizId);
        return bindings.stream()
                .map(persistentMapper::asNote)
                .filter(note -> permissionAccessService.checkUserPermissionToViewResource(note.id(), NOTE))
                .toList();
    }

    public List<Quiz> listQuizzesBoundToNote(UUID noteId, String userId) {
        checkIfNoteExists(noteId);
        List<QuizEntity> bindings = bindingRepository.findAllBoundQuizzesByNoteId(noteId);
        return bindings.stream()
                .map(binding -> persistentMapper.asQuiz(binding, userId))
                .filter(quiz -> permissionAccessService.checkUserPermissionToViewResource(quiz.id(), QUIZ))
                .toList();
    }

    private void checkIfNoteExists(UUID noteId) {
        noteService.getNoteById(noteId);
    }

    private void checkIfQuizExists(UUID quizId, String userId) {
        quizService.showQuizById(quizId, userId);
    }
}
