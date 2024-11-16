package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.ResourcesApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.comment.CommentService;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.model.CommentCreationDto;
import com.brightpath.learnify.model.CommentDto;
import com.brightpath.learnify.model.ResourceTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResourceController implements ResourcesApi {
    private final CommentService commentService;
    private final DtoMapper dtoMapper;
    private final UserIdentityService userIdentityService;

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#resourceId, #resourceType.name()) or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<CommentDto> addCommentToResource(ResourceTypeDto resourceType, UUID resourceId, CommentCreationDto commentCreationDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        Comment createdComment = commentService.addCommentToQuiz(dtoMapper.asCommentCreation(commentCreationDto,
                        convertedResourceType,
                        resourceId, userIdentityService.getCurrentUserId()
                ),
                resourceId,
                convertedResourceType);
        return ResponseEntity
                .status(CREATED)
                .body(dtoMapper.asCommentDto(createdComment));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#resourceId, #resourceType.name()) or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<List<CommentDto>> getCommentsToResource(ResourceTypeDto resourceType, UUID resourceId) {
        List<Comment> commentsForResource = commentService.getCommentsForResource(resourceId, userIdentityService.getCurrentUserId());
        return ResponseEntity.ok(commentsForResource.stream()
                .map(dtoMapper::asCommentDto)
                .toList());
    }
}
