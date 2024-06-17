package com.brightpath.learnify.persistance.binding;

import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import jakarta.persistence.*;
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

    @OneToOne
    @JoinColumn(name = "note", nullable = false)
    private NoteEntity note;

    @OneToOne
    @JoinColumn(name = "quiz", nullable = false)
    private QuizEntity quiz;
}
