package com.brightpath.learnify.domain.quiz.question;

import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.question.QuestionRepository;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.quiz.QuizResultsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.brightpath.learnify.domain.common.ResourceType.QUIZ;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final QuizService quizService;
    private final UserIdentityService userIdentityService;

    public List<Question> createQuestions(UUID quizId, List<Question> questions) {
        updateNumberOfQuestionsInQuiz(quizId, questions.size());
        List<QuestionEntity> questionEntities = questions.stream()
                .map(question -> new QuestionEntity(uuidProvider.generateUuid(),
                        question.question(),
                        question.type(),
                        quizId,
                        question.weight(),
                        question.choices(),
                        question.feedback(),
                        question.otherProperties()
                ))
                .toList();

        List<QuestionEntity> savedEntities = questionRepository.saveAll(questionEntities);
        return persistentMapper.asQuestions(savedEntities);
    }

    public List<Question> getQuestionsByQuizId(UUID quizId) {
        List<QuestionEntity> foundEntities = questionRepository.findAllByQuizId(quizId);
        return persistentMapper.asQuestions(foundEntities);
    }

    public List<Question> getIncorrectQuestionsByQuizId(UUID quizId) {
        Optional<QuizEntity> quizEntity = quizService.findQuizEntity(quizId);
        if (quizEntity.isEmpty()) {
            throw new ResourceNotFoundException(QUIZ);
        }
        Set<QuizResultsEntity> quizResults = quizEntity.get().getQuizResults();
        String userId = userIdentityService.getCurrentUserId();
        return quizResults.stream()
                .filter(result -> result.getUserId().equals(userId))
                .findFirst()
                .map(this::mapQuizResultsEntityIntoListOfIncorrectQuestions)
                .orElse(new ArrayList<>());
    }

    private List<Question> mapQuizResultsEntityIntoListOfIncorrectQuestions(QuizResultsEntity quizResultsEntity) {
        return quizResultsEntity.getIncorrectQuestions().stream()
                .map(persistentMapper::asQuestion)
                .toList();
    }

    public Question updateQuestion(UUID questionId, Question question) {
        if (quizService.findQuizEntity(question.quizId()).isEmpty()) {
            throw new ResourceNotFoundException(QUIZ);
        }
        QuestionEntity questionEntity = new QuestionEntity(questionId,
                question.question(),
                question.type(),
                question.quizId(),
                question.weight(),
                question.choices(),
                question.feedback(),
                question.otherProperties()
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
