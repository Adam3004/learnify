package com.brightpath.learnify.persistance.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {

    @Query("""
            SELECT u
            FROM QuizEntity u
            WHERE (:workspaceId IS NULL OR u.workspace.id = :workspaceId)
            """)
    List<QuizEntity> searchQuizzes(UUID workspaceId);

    @Query("SELECT e FROM QuizEntity e ORDER BY COALESCE((select r.lastTryDate FROM e.quizResults as r WHERE r.userId=:userId), e.createdAt) DESC")
    List<QuizEntity> findTop4RecentQuizzes(@Param("userId") String userId, Pageable pageable);
}
