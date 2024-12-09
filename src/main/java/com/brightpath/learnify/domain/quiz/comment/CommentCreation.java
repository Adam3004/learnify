package com.brightpath.learnify.domain.quiz.comment;

import com.brightpath.learnify.domain.common.ResourceType;

import java.util.Optional;
import java.util.UUID;

public record CommentCreation(
        String commentOwnerId,
        ResourceType resourceType,
        UUID resourceId,
        short rating,
        Optional<String> title,
        Optional<String> description
) {
}
