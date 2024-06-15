package com.brightpath.learnify.persistance.common;

import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.note.Note;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import org.springframework.stereotype.Component;

@Component
public class PersistentMapper {

    public Workspace asWorkspace(WorkspaceEntity entity) {
        return new Workspace(entity.getId(), entity.getDisplayName());
    }

    public User asUser(UserEntity entity) {
        return new User(entity.getId(), entity.getDisplayName(), entity.getEmail());
    }

    public Note asNote(NoteEntity entity) {
        return new Note(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                asWorkspace(entity.getWorkspace()),
                asUser(entity.getOwner()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
