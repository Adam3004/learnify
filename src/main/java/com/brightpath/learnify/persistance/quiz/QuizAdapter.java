package com.brightpath.learnify.persistance.quiz;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.result.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.result.QuizUserResult;
import com.brightpath.learnify.model.QuizCreationDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuizAdapter {
    @Transactional
    Optional<Quiz> createQuiz(String title, String description, UUID workspaceId, String ownerId, PermissionLevel permissionLevel);

    Optional<Quiz> getQuizById(UUID quizId, String userId);

    boolean quizEntityExits(UUID quizId);

    List<Quiz> getRecentQuizzes(String userId);

    List<QuizUserResult> getTopQuizResults(UUID quizId, int numberOfExpectedResults);

    List<Quiz> searchQuizzes(String userId, UUID workspaceId, String ownerId, String name, PermissionLevel permissionLevel, float averageRating);

    void deleteQuestion(UUID quizId, UUID questionId);

    Quiz updateQuizDetails(UUID quizId, QuizCreationDto quizCreationDto, String userId);

    QuizSimpleResult updateQuizResults(UUID quizId, String userId, QuizSimpleResult quizSimpleResult, List<UUID> incorrectIds);

    @Transactional
    void deleteQuiz(UUID quizId);

    void updateNumberOfQuestionsInQuiz(UUID quizId, List<Question> questions);

    List<Question> getIncorrectQuestionsByQuizId(UUID quizId, String userId);
}
