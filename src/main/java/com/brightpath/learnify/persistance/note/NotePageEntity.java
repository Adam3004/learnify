package com.brightpath.learnify.persistance.note;

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
@Table(name = "note_pages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotePageEntity {

        @Id
        @Column(name = "id", nullable = false, unique = true)
        private UUID id;

        @Column(name = "note_uuid", nullable = false)
        private UUID noteId;

        @Column(name = "page_number", nullable = false)
        private int pageNumber;

        @Column(name = "content", nullable = false, columnDefinition = "TEXT")
        private String content;
}
