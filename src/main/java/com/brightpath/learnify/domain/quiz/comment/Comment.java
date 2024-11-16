package com.brightpath.learnify.domain.quiz.comment;

import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.user.User;

import java.util.UUID;

public record Comment(UUID id,
                      User owner,
                      ResourceType resourceType,
                      UUID resourceId,
                      short rating,
                      String title,
                      String description) {
}
