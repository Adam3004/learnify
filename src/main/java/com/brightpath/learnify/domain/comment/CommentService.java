package com.brightpath.learnify.domain.comment;

import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.comment.CommentCreation;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToAddCommentException;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToViewCommentsException;
import com.brightpath.learnify.persistance.comment.CommentEntity;
import com.brightpath.learnify.persistance.comment.CommentRepository;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final CommentRepository commentRepository;
    private final PermissionAccessService permissionAccessService;

    public List<Comment> getCommentsForResource(UUID resourceId, ResourceType resourceType) {
        if (!permissionAccessService.checkUserPermissionToViewResource(resourceId, resourceType)) {
            throw new UserNotAuthorizedToViewCommentsException();
        }
        List<CommentEntity> commentsForResource = commentRepository.findAllByResourceId(resourceId);
        return commentsForResource.stream()
                .map(persistentMapper::asComment)
                .toList();
    }

    public Comment addCommentToQuiz(CommentCreation newComment, UUID resourceId, ResourceType resourceType) {
        if (!permissionAccessService.hasUserAccessToResource(newComment.commentOwnerId(), resourceId, resourceType, READ_ONLY)) {
            throw new UserNotAuthorizedToAddCommentException();
        }
        CommentEntity newCommentEntity = new CommentEntity(uuidProvider.generateUuid(),
                newComment.commentOwnerId(),
                resourceType,
                resourceId,
                newComment.rating(),
                newComment.title().orElse(null),
                newComment.description().orElse(null));
        commentRepository.save(newCommentEntity);
        return persistentMapper.asComment(newCommentEntity);
    }
}
