package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.CommentsApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.comment.CommentService;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.model.CommentCreationDto;
import com.brightpath.learnify.model.CommentDto;
import com.brightpath.learnify.model.ResourceTypeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController implements CommentsApi {
    private final CommentService commentService;
    private final DtoMapper dtoMapper;

    @Override
    public ResponseEntity<CommentDto> addCommentToResource(ResourceTypeDto resourceType, UUID resourceId, CommentCreationDto commentCreationDto) {
        ResourceType convertedResourceType = dtoMapper.fromResourceTypeDto(resourceType);
        Comment createdComment = commentService.addCommentToQuiz(dtoMapper.asCommentCreation(commentCreationDto, convertedResourceType, resourceId),
                resourceId,
                convertedResourceType);
        return ResponseEntity
                .status(CREATED)
                .body(dtoMapper.asCommentDto(createdComment));
    }

    @Override
    public ResponseEntity<List<CommentDto>> getCommentsToResource(ResourceTypeDto resourceType, UUID resourceId) {
        List<Comment> commentsForResource = commentService.getCommentsForResource(resourceId, dtoMapper.fromResourceTypeDto(resourceType));
        return ResponseEntity.ok(commentsForResource.stream()
                .map(dtoMapper::asCommentDto)
                .toList());
    }
}
