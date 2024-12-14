package com.brightpath.learnify.domain.quiz.question;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.exception.notfound.ResourceNotFoundException;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.question.QuestionRepository;
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
    private final QuestionRepository questionRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;
    private final QuizAdapter quizAdapter;

    @Transactional
    public List<Question> createQuestions(UUID quizId, List<Question> questions) {
        quizAdapter.updateNumberOfQuestionsInQuiz(quizId, questions.size());
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

    public List<Question> getIncorrectQuestionsByQuizId(UUID quizId, String userId) {
        return quizAdapter.getIncorrectQuestionsByQuizId(quizId, userId);
    }


    public Question updateQuestion(UUID questionId, Question question) {
        if (!quizAdapter.quizEntityExits(question.quizId())) {
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
}
