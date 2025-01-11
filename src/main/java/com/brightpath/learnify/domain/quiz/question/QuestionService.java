package com.brightpath.learnify.domain.quiz.question;

import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.domain.quiz.question.port.QuestionPersistencePort;
import com.brightpath.learnify.domain.quiz.port.QuizPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuizPersistencePort quizPersistencePort;
    private final QuestionPersistencePort questionPersistencePort;

    @Transactional
    public List<Question> createQuestions(UUID quizId, List<Question> questions) {
        List<Question> createdQuestions = questionPersistencePort.createQuestions(quizId, questions);
        updateNumberOfQuestionsInQuiz(quizId, createdQuestions);
        return createdQuestions;
    }

    public List<Question> getQuestionsByQuizId(UUID quizId) {
        return questionPersistencePort.getQuestionsByQuizId(quizId);
    }

    public List<Question> getIncorrectQuestionsByQuizId(UUID quizId, String userId) {
        return quizPersistencePort.getIncorrectQuestionsByQuizId(quizId, userId);
    }

    public Question updateQuestion(UUID questionId, Question question) {
        if (!quizPersistencePort.quizEntityExits(question.quizId())) {
            throw new ResourceNotFoundException(QUIZ);
        }
        return questionPersistencePort.updateQuestion(questionId, question);
    }

    public void updateNumberOfQuestionsInQuiz(UUID quizId, List<Question> questions) {
        quizPersistencePort.updateNumberOfQuestionsInQuiz(quizId, questions);
    }

    @Transactional
    public void deleteQuestion(UUID quizId, UUID questionId) {
        quizPersistencePort.deleteQuestion(quizId, questionId);
        questionPersistencePort.deleteQuestionById(questionId);
    }
}
