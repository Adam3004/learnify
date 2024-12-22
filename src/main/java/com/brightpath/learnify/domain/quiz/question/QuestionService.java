package com.brightpath.learnify.domain.quiz.question;

import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.question.QuestionAdapter;
import com.brightpath.learnify.persistance.quiz.QuizAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuizAdapter quizAdapter;
    private final QuestionAdapter questionAdapter;

    @Transactional
    public List<Question> createQuestions(UUID quizId, List<Question> questions) {
        List<Question> createdQuestions = questionAdapter.createQuestions(quizId, questions);
        updateNumberOfQuestionsInQuiz(quizId, createdQuestions);
        return createdQuestions;
    }

    public List<Question> getQuestionsByQuizId(UUID quizId) {
        return questionAdapter.getQuestionsByQuizId(quizId);
    }

    public List<Question> getIncorrectQuestionsByQuizId(UUID quizId, String userId) {
        return quizAdapter.getIncorrectQuestionsByQuizId(quizId, userId);
    }

    public Question updateQuestion(UUID questionId, Question question) {
        if (!quizAdapter.quizEntityExits(question.quizId())) {
            throw new ResourceNotFoundException(QUIZ);
        }
        return questionAdapter.updateQuestion(questionId, question);
    }

    public void updateNumberOfQuestionsInQuiz(UUID quizId, List<Question> questions) {
        quizAdapter.updateNumberOfQuestionsInQuiz(quizId, questions);
    }

    @Transactional
    public void deleteQuestion(UUID quizId, UUID questionId) {
        quizAdapter.deleteQuestion(quizId, questionId);
        questionAdapter.deleteQuestionById(questionId);
    }
}
