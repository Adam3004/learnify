package com.brightpath.learnify.domain.comment;

import com.brightpath.learnify.domain.quiz.comment.Comment;
import com.brightpath.learnify.domain.quiz.comment.CommentCreation;
import com.brightpath.learnify.persistance.comment.CommentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentAdapter commentAdapter;

    public List<Comment> getCommentsForResource(UUID resourceId) {
        return commentAdapter.getCommentsForResource(resourceId);
    }

    public Comment addCommentToResource(CommentCreation newComment) {
        return commentAdapter.addCommentToResource(newComment);
    }
}
