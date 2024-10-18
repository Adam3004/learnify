package com.brightpath.learnify.persistance.binding;

import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
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

import java.util.UUID;

@Entity
@Table(name = "bindings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BindingEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "note", nullable = false, unique = false)
    private NoteEntity note;

    @ManyToOne
    @JoinColumn(name = "quiz", nullable = false, unique = false)
    private QuizEntity quiz;
}
