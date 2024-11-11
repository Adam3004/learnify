package com.brightpath.learnify.persistance.comment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * comments shouldn't have accesses etc. It's visible for every user with at least RO to note/quiz
 **/

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentEntity {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "author", nullable = false)
    private String ownerId;

    //rating is a number from 1 to 5. That's all possible rates for quiz or note
    @Column(name = "rating", nullable = false)
    private Short rating;

    @Column(name = "title", nullable = true)
    private String title;

    @Column(name = "description", nullable = true)
    private String description;
}
