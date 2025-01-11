package com.brightpath.learnify.domain.comment;

import com.brightpath.learnify.domain.comment.port.CommentPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentPersistencePort commentPersistencePort;

    public List<Comment> getCommentsForResource(UUID resourceId) {
        return commentPersistencePort.getCommentsForResource(resourceId);
    }

    public Comment addCommentToResource(CommentCreation newComment) {
        return commentPersistencePort.addCommentToResource(newComment);
    }
}
