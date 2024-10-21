package com.brightpath.learnify.persistance.question;

import com.brightpath.learnify.domain.quiz.question.QuestionType;
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
@Table(name = "questions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuestionEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "question_type", nullable = false)
    private QuestionType type;

    @Column(name = "quiz_id", nullable = false)
    private UUID quizId;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "choices", nullable = false, columnDefinition = "TEXT")
    private String choices;

    @Column(name = "feedback", nullable = false, columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "otherProperties", nullable = false, columnDefinition = "TEXT")
    private String otherProperties;
}
