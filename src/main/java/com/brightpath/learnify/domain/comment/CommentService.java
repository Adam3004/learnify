package com.brightpath.learnify.domain.comment;

import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.comment.CommentCreation;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import com.brightpath.learnify.persistance.comment.CommentEntity;
import com.brightpath.learnify.persistance.comment.CommentRepository;
import com.brightpath.learnify.persistance.common.PersistentMapper;
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
    private final UserService userService;

    public List<Comment> getCommentsForResource(UUID resourceId, String userId) {
        List<CommentEntity> commentsForResource = commentRepository.findAllByResourceId(resourceId);
        User owner = userService.getUserById(userId);
        return commentsForResource.stream()
                .map(comment -> persistentMapper.asComment(comment, owner))
                .toList();
    }

    public Comment addCommentToQuiz(CommentCreation newComment, UUID resourceId, ResourceType resourceType) {
        CommentEntity newCommentEntity = new CommentEntity(uuidProvider.generateUuid(),
                newComment.commentOwnerId(),
                resourceType,
                resourceId,
                newComment.rating(),
                newComment.title().orElse(null),
                newComment.description().orElse(null));
        CommentEntity savedComment = commentRepository.save(newCommentEntity);
        User owner = userService.getUserById(newComment.commentOwnerId());
        return persistentMapper.asComment(savedComment, owner);
    }
}
