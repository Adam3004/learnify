package com.brightpath.learnify.persistance.quiz;

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
@Table(name = "quiz_results")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizResultsEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "last_number_of_correct")
    private Integer lastNumberOfCorrect;

    @Column(name = "last_number_of_incorrect")
    private Integer lastNumberOfIncorrect;

    @Column(name = "best_number_of_correct")
    private Integer bestNumberOfCorrect;

    @Column(name = "best_number_of_incorrect")
    private Integer bestNumberOfIncorrect;
}
