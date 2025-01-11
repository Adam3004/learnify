package com.brightpath.learnify.persistance.question;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.quiz.QuizResultsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionDBAdapter implements QuestionAdapter{
    private final QuestionRepository questionRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;

    public List<Question> createQuestions(UUID quizId, List<Question> questions) {
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

    public Question updateQuestion(UUID questionId, Question question) {
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

    public List<QuestionEntity> getQuestionEntitiesForIds(List<UUID> questionIds) {
        return questionRepository.findAllById(questionIds);
    }

    public List<Question> getQuestionsByQuizId(UUID quizId) {
        List<QuestionEntity> foundEntities = questionRepository.findAllByQuizId(quizId);
        return persistentMapper.asQuestions(foundEntities);
    }

    public void deleteQuestionById(UUID questionId) {
        questionRepository.deleteById(questionId);
    }

    public void updateQuizResultWithNewQuestions(QuizResultsEntity quizResultsForUser, List<UUID> incorrectIds, UUID quizId) {
        questionRepository.findAllByQuizId(quizId).stream()
                .filter(question -> incorrectIds.contains(question.getId()))
                .forEach(question -> quizResultsForUser.getIncorrectQuestions().add(question));
    }
}
