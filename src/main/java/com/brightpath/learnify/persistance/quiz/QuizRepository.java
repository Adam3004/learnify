package com.brightpath.learnify.persistance.quiz;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
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
        JOIN PermissionsAccessEntity access ON u.id = access.resourceId
        WHERE
            (:workspaceId IS NULL OR u.workspace.id = :workspaceId)
            AND (:ownerId IS NULL OR u.author.id = :ownerId)
            AND (:titlePart IS NULL OR lower(u.title) LIKE %:titlePart%)
            AND (:permissionLevel IS NULL OR access.permissionLevel = :permissionLevel)
            AND (
                (access.permissionLevel = 1)
                OR (u.author.id = :userId)
                OR (:userId IN (SELECT user.userId FROM access.permissions user))
            )
        """)
    List<QuizEntity> searchQuizzes(String userId, UUID workspaceId, String ownerId, String titlePart, PermissionLevel permissionLevel);

    @Query("SELECT e FROM QuizEntity e ORDER BY COALESCE((select r.lastTryDate FROM e.quizResults as r WHERE r.userId=:userId), e.createdAt) DESC")
    List<QuizEntity> findTop4RecentQuizzes(@Param("userId") String userId, Pageable pageable);
}
