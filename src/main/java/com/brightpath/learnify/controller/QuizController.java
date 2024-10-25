package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.QuizzesApi;
import com.brightpath.learnify.domain.auth.PermissionAccessService;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.common.ResourceType;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.question.QuestionService;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToEditException;
import com.brightpath.learnify.exception.authorization.UserNotAuthorizedToGetException;
import com.brightpath.learnify.model.QuestionCreationDto;
import com.brightpath.learnify.model.QuestionDto;
import com.brightpath.learnify.model.QuizCreationDto;
import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizResultUpdateDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_ONLY;
import static com.brightpath.learnify.domain.auth.permission.ResourceAccessEnum.READ_WRITE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QuizController implements QuizzesApi {
    private final QuizService quizService;
    private final QuestionService questionService;
    private final UserIdentityService userIdentityService;
    private final DtoMapper dtoMapper;
    private final PermissionAccessService permissionAccessService;

    @Override
    public ResponseEntity<QuizDetailsDto> createQuiz(QuizCreationDto quizCreationDto) {
        throwIfUserDoesNotHavePermissionToEditResource(quizCreationDto.getWorkspaceId(), ResourceType.WORKSPACE);
        String userId = userIdentityService.getCurrentUserId();
        Optional<Quiz> quiz = quizService.createQuiz(quizCreationDto.getTitle(), quizCreationDto.getDescription(),
                quizCreationDto.getWorkspaceId(), userId);
        return quiz
                .map(quizToConvert -> ResponseEntity.status(CREATED).body(dtoMapper.asQuizDetailsDto(quizToConvert)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<QuizDetailsDto> showDetailsQuizById(UUID quizId) {
        throwIfUserDoesNotHavePermissionToViewResource(quizId, ResourceType.QUIZ);
        Quiz quiz = quizService.showQuizById(quizId);
        return ResponseEntity
                .status(OK)
                .body(dtoMapper.asQuizDetailsDto(quiz));
    }

    @Override
    public ResponseEntity<QuizSummaryDto> showQuizById(UUID quizId) {
        throwIfUserDoesNotHavePermissionToViewResource(quizId, ResourceType.QUIZ);
        Quiz quiz = quizService.showQuizById(quizId);
        return ResponseEntity
                .status(OK)
                .body(dtoMapper.asQuizSummaryDto(quiz));
    }


    @Override
    public ResponseEntity<List<QuestionDto>> createQuestions(UUID quizId, List<@Valid QuestionCreationDto> questionCreationDto) {
        throwIfUserDoesNotHavePermissionToEditResource(quizId, ResourceType.QUIZ);
        List<Question> questions = questionCreationDto.stream()
                .map(currentDto -> dtoMapper.fromQuestionCreationDto(currentDto, quizId))
                .toList();
        List<Question> createdQuestions = questionService.createQuestions(quizId, questions);
        return ResponseEntity.status(CREATED).body(createdQuestions.stream()
                .map(dtoMapper::asQuestionDto)
                .toList());
    }

    @Override
    public ResponseEntity<List<QuestionDto>> showQuestionsByQuizId(UUID quizId) {
        throwIfUserDoesNotHavePermissionToViewResource(quizId, ResourceType.QUIZ);
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.status(OK).body(questions.stream()
                .map(dtoMapper::asQuestionDto)
                .toList());
    }

    @Override
    public ResponseEntity<List<QuizSummaryDto>> listRecentQuizzes() {
        List<Quiz> quizzes = quizService.listRecentQuizzes();
        return ResponseEntity.status(OK).body(quizzes.stream()
                .map(dtoMapper::asQuizSummaryDto)
                .toList());
    }

    @Override
    public ResponseEntity<QuestionDto> updateQuestion(UUID quizId, UUID questionId, QuestionDto questionDto) {
        throwIfUserDoesNotHavePermissionToEditResource(questionId, ResourceType.QUIZ);
        Question question = dtoMapper.fromQuestionDto(questionId, questionDto, quizId);
        Question updatedQuestion = questionService.updateQuestion(questionId, question);
        return ResponseEntity.status(OK).body(dtoMapper.asQuestionDto(updatedQuestion));
    }

    @Override
    public ResponseEntity<QuizResultUpdateDto> updateResultsByQuizId(UUID quizId, QuizResultUpdateDto quizResultUpdateDto) {
        throwIfUserDoesNotHavePermissionToEditResource(quizId, ResourceType.QUIZ);
        QuizSimpleResult quizSimpleResult = quizService.updateQuizResult(quizId,
                dtoMapper.asQuizSimpleResult(quizResultUpdateDto));
        return ResponseEntity.ok(dtoMapper.asQuizResultUpdateDto(quizSimpleResult));
    }

    private void throwIfUserDoesNotHavePermissionToEditResource(UUID resourceId, ResourceType resourceType) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToEditNote = permissionAccessService.hasUserAccessToResource(userId, resourceId, resourceType, READ_WRITE);
        if (!hasAccessToEditNote) {
            throw new UserNotAuthorizedToEditException();
        }
    }

    private void throwIfUserDoesNotHavePermissionToViewResource(UUID resourceId, ResourceType resourceType) {
        String userId = userIdentityService.getCurrentUserId();
        boolean hasAccessToEditNote = permissionAccessService.hasUserAccessToResource(userId, resourceId, resourceType, READ_ONLY);
        if (!hasAccessToEditNote) {
            throw new UserNotAuthorizedToGetException();
        }
    }

    //todo increment numberOfQuestions when adding questions
}