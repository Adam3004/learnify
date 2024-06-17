package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizRepository;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {
    @PersistenceContext
    private EntityManager entityManager;
    private final QuizRepository quizRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;

    public Optional<Quiz> createQuiz(String title, String description, UUID workspaceId, UUID ownerId) {
        WorkspaceEntity workspace = entityManager.getReference(WorkspaceEntity.class, workspaceId);
        UserEntity author = entityManager.getReference(UserEntity.class, ownerId);
        QuizEntity quizEntity = new QuizEntity(uuidProvider.generateUuid(),
                workspace, title,
                description,
                0,
                null,
                null,
                null,
                null,
                author,
                null,
                OffsetDateTime.now(Clock.systemUTC())
        );
        QuizEntity result = quizRepository.save(quizEntity);
        return Optional.of(persistentMapper.asQuiz(result));
    }

    public Optional<Quiz> showQuizById(UUID quizId) {
        Optional<QuizEntity> quizEntity = quizRepository.findById(quizId);
        return quizEntity.map(persistentMapper::asQuiz);
    }

    public List<Quiz> listRecentQuizzes() {
        List<QuizEntity> quizzes = quizRepository.findTop4RecentQuizzes();
        return quizzes.stream()
                .map(persistentMapper::asQuiz)
                .toList();
    }
}
