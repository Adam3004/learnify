package com.brightpath.learnify.persistance.note;

import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoteEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "workspace", nullable = false)
    private WorkspaceEntity workspace;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private UserEntity owner;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
