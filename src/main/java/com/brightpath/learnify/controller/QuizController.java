package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.QuizzesApi;
import com.brightpath.learnify.domain.auth.AuthorizationService;
import com.brightpath.learnify.domain.questionService.QuestionService;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.model.QuestionCreationDto;
import com.brightpath.learnify.model.QuestionDto;
import com.brightpath.learnify.model.QuizCreationDto;
import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import com.brightpath.learnify.persistance.question.Question;
import com.brightpath.learnify.persistance.quiz.Quiz;
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

    @Override
    public ResponseEntity<QuizDetailsDto> createQuiz(QuizCreationDto quizCreationDto) {
        Optional<Quiz> quiz = quizService.createQuiz(quizCreationDto.getTitle(), quizCreationDto.getDescription(),
                quizCreationDto.getNumberOfQuestions(), UUID.fromString(quizCreationDto.getWorkspaceId()), authorizationService.defaultUser().uuid());
        return quiz
                .map(quizToConvert -> ResponseEntity.status(CREATED).body(quizToConvert.convertToQuizDetailsDto()))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<QuizDetailsDto> showDetailsQuizById(String quizId) {
        Optional<Quiz> quiz = quizService.showQuizById(UUID.fromString(quizId));
        return quiz
                .map(quizToConvert -> ResponseEntity.status(OK).body(quizToConvert.convertToQuizDetailsDto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<QuizSummaryDto> showQuizById(String quizId) {
        Optional<Quiz> quiz = quizService.showQuizById((UUID.fromString(quizId)));
        return quiz
                .map(quizToConvert -> ResponseEntity.status(OK).body(quizToConvert.convertToQuizSummaryDto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Override
    public ResponseEntity<List<QuestionDto>> createQuestions(String quizId, List<@Valid QuestionCreationDto> questionCreationDto) {
        List<Question> questions = questionCreationDto.stream()
                .map(currentDto -> new Question(currentDto, UUID.fromString(quizId)))
                .toList();
        List<Question> createdQuestions = questionService.createQuestions(UUID.fromString(quizId), questions);
        return ResponseEntity.status(CREATED).body(createdQuestions.stream()
                .map(Question::convertToQuestionDto)
                .toList());
    }

    @Override
    public ResponseEntity<List<QuestionDto>> showQuestionsByQuizId(String quizId) {
        List<Question> questions = questionService.getQuestionsByQuizId(UUID.fromString(quizId));
        return ResponseEntity.status(OK).body(questions.stream()
                .map(Question::convertToQuestionDto)
                .toList());
    }

    //todo createdAt
    //todo get last 4
    //todo increment numberOfQuestions when adding questions
}