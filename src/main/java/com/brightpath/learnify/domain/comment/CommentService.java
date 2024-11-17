package com.brightpath.learnify.domain.comment;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.comment.CommentCreation;
import com.brightpath.learnify.persistance.comment.CommentEntity;
import com.brightpath.learnify.persistance.comment.CommentRepository;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.user.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;

    public List<Comment> getCommentsForResource(UUID resourceId) {
        List<CommentEntity> commentsForResource = commentRepository.findAllByResourceId(resourceId);
        return commentsForResource.stream()
                .map(persistentMapper::asComment)
                .toList();
    }

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
        return persistentMapper.asComment(savedComment);
    }
}
