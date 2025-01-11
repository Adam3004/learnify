package com.brightpath.learnify.persistance.question;

import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.persistance.quiz.QuizResultsEntity;

import java.util.List;
import java.util.UUID;

public interface QuestionAdapter {
    List<Question> createQuestions(UUID quizId, List<Question> questions);

    Question updateQuestion(UUID questionId, Question question);

    List<QuestionEntity> getQuestionEntitiesForIds(List<UUID> questionIds);

    List<Question> getQuestionsByQuizId(UUID quizId);

    void deleteQuestionById(UUID questionId);

    void updateQuizResultWithNewQuestions(QuizResultsEntity quizResultsForUser, List<UUID> incorrectIds, UUID quizId);
}
