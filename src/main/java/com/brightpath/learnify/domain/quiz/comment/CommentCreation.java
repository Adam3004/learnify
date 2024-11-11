package com.brightpath.learnify.domain.quiz.comment;

import java.util.Optional;

public record CommentCreation(
        String commentOwnerId,
        short rating,
        Optional<String> title,
        Optional<String> description
) {
}
