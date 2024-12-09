package com.brightpath.learnify.domain.quiz;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.quiz.result.QuizSimpleResult;
import com.brightpath.learnify.domain.common.RatingStats;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder(toBuilder = true)
public record Quiz(UUID id,
                   Workspace workspace,
                   String title,
                   String description,
                   int numberOfQuestions,
                   QuizSimpleResult lastScore,
                   QuizSimpleResult bestScore,
                   User author,
                   OffsetDateTime createdAt,
                   PermissionLevel permissionLevel,
                   RatingStats ratingStats) {

    public String findScore() {
        if (numberOfQuestions == 0 || lastScore == null) {
            return "-1";
        }
        double score = 100 * lastScore.correct() / (double) numberOfQuestions;
        return score + "%";
    }
}
