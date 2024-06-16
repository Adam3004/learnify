package com.brightpath.learnify.persistance.quiz;

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

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "quizes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "workspace", nullable = false)
    private WorkspaceEntity workspace;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "number_of_questions", nullable = false)
    private int numberOfQuestions;

    @Column(name = "last_number_of_correct")
    private Integer lastNumberOfCorrect;

    @Column(name = "last_number_of_incorrect")
    private Integer lastNumberOfIncorrect;

    @Column(name = "best_number_of_correct")
    private Integer bestNumberOfCorrect;

    @Column(name = "best_number_of_incorrect")
    private Integer bestNumberOfIncorrect;


    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    private UserEntity author;

    @Column(name = "lastTryDate")
    private ZonedDateTime lastTryDate;

//    UUID id,
//                   Workspace workspace,
//                   String title,
//                   String description,
//                   int numberOfQuestions,
//                   QuizSimpleResult lastScore,
//                   QuizSimpleResult bestScore,
//                   User author,
//                   ZonedDateTime lastTryDate
}
