package com.brightpath.learnify.persistance.workspace;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "workspaces")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkspaceEntity {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID id;

    @Column(name = "display_name", nullable = false)
    private String displayName;
}
