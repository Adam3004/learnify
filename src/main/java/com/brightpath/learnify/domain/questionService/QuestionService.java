package com.brightpath.learnify.domain.questionService;

import com.brightpath.learnify.domain.common.UuidProvider;
import com.brightpath.learnify.persistance.common.PersistentMapper;
import com.brightpath.learnify.persistance.question.Question;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final PersistentMapper persistentMapper;
    private final UuidProvider uuidProvider;

    public List<Question> createQuestions(UUID quizId, List<Question> questions) {
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
}
