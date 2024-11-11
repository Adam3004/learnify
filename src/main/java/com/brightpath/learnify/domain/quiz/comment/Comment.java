package com.brightpath.learnify.domain.quiz.comment;

import java.util.UUID;

public record Comment(UUID id,
                      String commentOwnerId,
                      short rating,
                      String title,
                      String description) {
}
