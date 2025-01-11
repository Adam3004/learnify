package com.brightpath.learnify.domain.comment.port;

import com.brightpath.learnify.domain.comment.Comment;
import com.brightpath.learnify.domain.comment.CommentCreation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CommentPersistencePort {
    List<Comment> getCommentsForResource(UUID resourceId);

    @Transactional
    Comment addCommentToResource(CommentCreation newComment);
}
