package com.brightpath.learnify.persistance.comment;

import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.comment.CommentCreation;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.common.RatingsEmbeddableEntity;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.note.NoteRepository;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizRepository;
import com.brightpath.learnify.persistance.user.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentDBAdapter implements CommentAdapter{
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;
    private final NoteRepository noteRepository;
    private final QuizRepository quizRepository;

    public List<Comment> getCommentsForResource(UUID resourceId) {
        List<CommentEntity> commentsForResource = commentRepository.findAllByResourceId(resourceId);
        return commentsForResource.stream()
                .map(persistentMapper::asComment)
                .toList();
    }

    @Transactional
    public Comment addCommentToResource(CommentCreation newComment) {
        UserEntity owner = entityManager.getReference(UserEntity.class, newComment.commentOwnerId());
        CommentEntity newCommentEntity = new CommentEntity(uuidProvider.generateUuid(),
                owner,
                newComment.resourceType(),
                newComment.resourceId(),
                newComment.rating(),
                newComment.title().orElse(null),
                newComment.description().orElse(null));
        CommentEntity savedComment = commentRepository.save(newCommentEntity);
        RatingsEmbeddableEntity ratingsForResource = getRatingsForResource(newComment.resourceType(), newComment.resourceId());
        ratingsForResource.addRating(newComment.rating());
        return persistentMapper.asComment(savedComment);
    }

    private RatingsEmbeddableEntity getRatingsForResource(ResourceType resourceType, UUID resourceId) {
        switch (resourceType) {
            case NOTE -> {
                return noteRepository.findById(resourceId)
                        .map(NoteEntity::getRatings)
                        .orElseThrow(() -> new IllegalArgumentException("Note not found"));
            }
            case QUIZ -> {
                return quizRepository.findById(resourceId)
                        .map(QuizEntity::getRatings)
                        .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));
            }
            default -> {
                throw new IllegalArgumentException("Resource type not supported");
            }
        }
    }
}
