package com.brightpath.learnify.persistance.workspace;

import com.brightpath.learnify.persistance.user.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkspaceEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private UserEntity owner;

    @OneToMany(mappedBy = "parentWorkspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkspaceEntity> subWorkspaces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_workspace_id")
    private WorkspaceEntity parentWorkspace;

    @Override
    public String toString() {
        return "WorkspaceEntity{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", owner=" + owner +
                ", subWorkspaces=" + subWorkspaces +
                ", parentWorkspace=" + parentWorkspace.getId() +
                '}';
    }
}