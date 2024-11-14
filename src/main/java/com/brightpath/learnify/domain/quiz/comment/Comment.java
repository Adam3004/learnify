package com.brightpath.learnify.domain.quiz.comment;

import com.brightpath.learnify.domain.common.ResourceType;

import java.util.UUID;

public record Comment(UUID id,
                      String commentOwnerId,
                      ResourceType resourceType,
                      UUID resourceId,
                      short rating,
                      String title,
                      String description) {
}
