package com.brightpath.learnify.domain.quiz.question;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.question.QuestionRepository;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final QuizService quizService;

    public List<Question> createQuestions(UUID quizId, List<Question> questions) {
        updateNumberOfQuestionsInQuiz(quizId, questions.size());
        List<QuestionEntity> questionEntities = questions.stream()
                .map(question -> new QuestionEntity(uuidProvider.generateUuid(),
                        question.getQuestion(),
                        question.getType(),
                        quizId,
                        question.getWeight(),
                        question.getChoices(),
                        question.getFeedback(),
                        question.getOtherProperties()
                ))
                .toList();

        List<QuestionEntity> savedEntities = questionRepository.saveAll(questionEntities);
        return persistentMapper.asQuestions(savedEntities);
    }

    public List<Question> getQuestionsByQuizId(UUID quizId) {
        List<QuestionEntity> foundEntities = questionRepository.findAllByQuizId(quizId);
        return persistentMapper.asQuestions(foundEntities);
    }

    public Question updateQuestion(UUID questionId, Question question) {
        QuestionEntity questionEntity = new QuestionEntity(questionId,
                question.getQuestion(),
                question.getType(),
                question.getQuizId(),
                question.getWeight(),
                question.getChoices(),
                question.getFeedback(),
                question.getOtherProperties()
        );
        QuestionEntity updatedEntity = questionRepository.save(questionEntity);
        return persistentMapper.asQuestion(updatedEntity);
    }

    private void updateNumberOfQuestionsInQuiz(UUID quizId, int numberOfQuestions) {
        Optional<QuizEntity> foundQuiz = quizService.findQuizEntity(quizId);
        if (foundQuiz.isPresent()) {
            QuizEntity quiz = foundQuiz.get();
            quiz.setNumberOfQuestions(quiz.getNumberOfQuestions() + numberOfQuestions);
            quizService.updateQuiz(quiz);
        }
    }
}
