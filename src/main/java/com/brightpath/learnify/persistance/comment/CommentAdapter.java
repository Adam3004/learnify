package com.brightpath.learnify.persistance.comment;

import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.comment.CommentCreation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CommentAdapter {
    List<Comment> getCommentsForResource(UUID resourceId);

    @Transactional
    Comment addCommentToResource(CommentCreation newComment);
}
