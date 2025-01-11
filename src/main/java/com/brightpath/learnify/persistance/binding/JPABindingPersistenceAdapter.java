package com.brightpath.learnify.persistance.binding;

import com.brightpath.learnify.domain.binding.Binding;
import com.brightpath.learnify.domain.binding.port.BindingPersistencePort;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JPABindingPersistenceAdapter implements BindingPersistencePort {
    @PersistenceContext
    private EntityManager entityManager;
    private final UuidProvider uuidProvider;
    private final BindingRepository bindingRepository;
    private final PersistentMapper persistentMapper;

    public Binding createBinding(UUID noteId, UUID quizId) {
        NoteEntity note = entityManager.getReference(NoteEntity.class, noteId);
        QuizEntity quiz = entityManager.getReference(QuizEntity.class, quizId);
        BindingEntity binding = new BindingEntity(uuidProvider.generateUuid(), note, quiz);
        BindingEntity save = bindingRepository.save(binding);
        return new Binding(save.getId(), save.getNote().getId(), save.getQuiz().getId());
    }

    public List<Note> listNotesBoundToQuiz(UUID quizId, String userId) {
        List<NoteEntity> bindings = bindingRepository.findAllBoundNotesByQuizId(quizId);
        return bindings.stream()
                .map(note -> persistentMapper.asNote(note, userId))
                .toList();
    }

    public List<Quiz> listQuizzesBoundToNote(UUID noteId, String userId) {
        List<QuizEntity> bindings = bindingRepository.findAllBoundQuizzesByNoteId(noteId);
        return bindings.stream()
                .map(binding -> persistentMapper.asQuiz(binding, userId))
                .toList();
    }

    @Transactional
    public void removeBindingForQuiz(UUID quizId) {
        bindingRepository.deleteAllByQuizId(quizId);
    }

    @Transactional
    public void removeBindingForNote(UUID noteId) {
        bindingRepository.deleteAllByNoteId(noteId);
    }
}
