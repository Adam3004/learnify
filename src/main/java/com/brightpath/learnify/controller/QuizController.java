package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.QuizzesApi;
import com.brightpath.learnify.domain.auth.AuthorizationService;
import com.brightpath.learnify.domain.mapper.DtoMapper;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.question.QuestionService;
import com.brightpath.learnify.model.QuestionCreationDto;
import com.brightpath.learnify.model.QuestionDto;
import com.brightpath.learnify.model.QuizCreationDto;
import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizResultUpdateDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
public class QuizController implements QuizzesApi {
    private final QuizService quizService;
    private final QuestionService questionService;
    private final AuthorizationService authorizationService;
    private final DtoMapper dtoMapper;

    @Override
    public ResponseEntity<QuizDetailsDto> createQuiz(QuizCreationDto quizCreationDto) {
        Optional<Quiz> quiz = quizService.createQuiz(quizCreationDto.getTitle(), quizCreationDto.getDescription(),
                quizCreationDto.getWorkspaceId(), authorizationService.defaultUser().id());
        return quiz
                .map(quizToConvert -> ResponseEntity.status(CREATED).body(quizToConvert.convertToQuizDetailsDto()))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<QuizDetailsDto> showDetailsQuizById(UUID quizId) {
        Quiz quiz = quizService.showQuizById(quizId);
        return ResponseEntity
                .status(OK)
                .body(quiz.convertToQuizDetailsDto());
    }

    @Override
    public ResponseEntity<QuizSummaryDto> showQuizById(UUID quizId) {
        Quiz quiz = quizService.showQuizById(quizId);
        return ResponseEntity
                .status(OK)
                .body(quiz.convertToQuizSummaryDto());
    }


    @Override
    public ResponseEntity<List<QuestionDto>> createQuestions(UUID quizId, List<@Valid QuestionCreationDto> questionCreationDto) {
        List<Question> questions = questionCreationDto.stream()
                .map(currentDto -> new Question(currentDto, quizId))
                .toList();
        List<Question> createdQuestions = questionService.createQuestions(quizId, questions);
        return ResponseEntity.status(CREATED).body(createdQuestions.stream()
                .map(Question::convertToQuestionDto)
                .toList());
    }

    @Override
    public ResponseEntity<List<QuestionDto>> showQuestionsByQuizId(UUID quizId) {
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.status(OK).body(questions.stream()
                .map(Question::convertToQuestionDto)
                .toList());
    }

    @Override
    public ResponseEntity<List<QuizSummaryDto>> listRecentQuizzes() {
        List<Quiz> quizzes = quizService.listRecentQuizzes();
        return ResponseEntity.status(OK).body(quizzes.stream()
                .map(Quiz::convertToQuizSummaryDto)
                .toList());
    }

    @Override
    public ResponseEntity<QuestionDto> updateQuestion(UUID quizId, UUID questionId, QuestionDto questionDto) {
        Question question = new Question(questionDto, quizId, questionId);
        Question updatedQuestion = questionService.updateQuestion(questionId, question);
        return ResponseEntity.status(OK).body(updatedQuestion.convertToQuestionDto());
    }

    @Override
    public ResponseEntity<QuizResultUpdateDto> updateResultsByQuizId(UUID quizId, QuizResultUpdateDto quizResultUpdateDto) {
        QuizSimpleResult quizSimpleResult = quizService.updateQuizResult(quizId,
                dtoMapper.asQuizSimpleResult(quizResultUpdateDto));
        return ResponseEntity.ok(dtoMapper.asQuizResultUpdateDto(quizSimpleResult));
    }

    //todo increment numberOfQuestions when adding questions
}