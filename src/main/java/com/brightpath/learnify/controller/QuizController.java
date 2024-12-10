package com.brightpath.learnify.controller;

import com.brightpath.learnify.api.QuizzesApi;
import com.brightpath.learnify.controller.mapper.DtoMapper;
import com.brightpath.learnify.domain.auth.UserIdentityService;
import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.question.QuestionService;
import com.brightpath.learnify.domain.quiz.result.QuizSimpleResult;
import com.brightpath.learnify.domain.quiz.result.QuizUserResult;
import com.brightpath.learnify.model.QuestionCreationDto;
import com.brightpath.learnify.model.QuestionDto;
import com.brightpath.learnify.model.QuizBestResultDto;
import com.brightpath.learnify.model.QuizCreationDto;
import com.brightpath.learnify.model.QuizDetailsDto;
import com.brightpath.learnify.model.QuizResultUpdateDto;
import com.brightpath.learnify.model.QuizSummaryDto;
import com.brightpath.learnify.model.ResourceAccessTypeDto;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#quizCreationDto.workspaceId, 'WORKSPACE') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<QuizDetailsDto> createQuiz(QuizCreationDto quizCreationDto) {
        String userId = userIdentityService.getCurrentUserId();
        Optional<Quiz> quiz = quizService.createQuiz(quizCreationDto.getTitle(), quizCreationDto.getDescription(),
                quizCreationDto.getWorkspaceId(), userId, dtoMapper.fromResourceAccessTypeDto(quizCreationDto.getResourceAccessTypeDto()));
        return quiz
                .map(quizToConvert -> ResponseEntity.status(CREATED).body(dtoMapper.asQuizDetailsDto(quizToConvert)))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<QuizDetailsDto> showDetailsQuizById(UUID quizId) {
        String userId = userIdentityService.getCurrentUserId();
        Quiz quiz = quizService.showQuizById(quizId, userId);
        return ResponseEntity
                .status(OK)
                .body(dtoMapper.asQuizDetailsDto(quiz));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<QuizSummaryDto> showQuizById(UUID quizId) {
        String userId = userIdentityService.getCurrentUserId();
        Quiz quiz = quizService.showQuizById(quizId, userId);
        return ResponseEntity
                .status(OK)
                .body(dtoMapper.asQuizSummaryDto(quiz));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<List<QuestionDto>> createQuestions(UUID quizId, List<@Valid QuestionCreationDto> questionCreationDto) {
        List<Question> questions = questionCreationDto.stream()
                .map(currentDto -> dtoMapper.fromQuestionCreationDto(currentDto, quizId))
                .toList();
        List<Question> createdQuestions = questionService.createQuestions(quizId, questions);
        return ResponseEntity.status(CREATED).body(createdQuestions.stream()
                .map(dtoMapper::asQuestionDto)
                .toList());
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<List<QuestionDto>> showQuestionsByQuizId(UUID quizId) {
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.status(OK).body(questions.stream()
                .map(dtoMapper::asQuestionDto)
                .toList());
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<List<QuestionDto>> getIncorrectQuestionsByQuizId(UUID quizId) {
        String userId = userIdentityService.getCurrentUserId();
        List<Question> questions = questionService.getIncorrectQuestionsByQuizId(quizId, userId);
        return ResponseEntity.status(OK).body(questions.stream()
                .map(dtoMapper::asQuestionDto)
                .toList());
    }

    @Override
    public ResponseEntity<List<QuizSummaryDto>> listRecentQuizzes() {
        String userId = userIdentityService.getCurrentUserId();
        List<Quiz> quizzes = quizService.listRecentQuizzes(userId);
        return ResponseEntity.status(OK).body(quizzes.stream()
                .map(dtoMapper::asQuizSummaryDto)
                .toList());
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<QuestionDto> updateQuestion(UUID quizId, UUID questionId, QuestionDto questionDto) {
        Question question = dtoMapper.fromQuestionDto(questionId, questionDto, quizId);
        Question updatedQuestion = questionService.updateQuestion(questionId, question);
        return ResponseEntity.status(OK).body(dtoMapper.asQuestionDto(updatedQuestion));
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToEditResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<QuizResultUpdateDto> updateResultsByQuizId(UUID quizId, QuizResultUpdateDto quizResultUpdateDto) {
        String userId = userIdentityService.getCurrentUserId();
        QuizSimpleResult quizSimpleResult = quizService.updateQuizResult(quizId,
                dtoMapper.asQuizSimpleResult(quizResultUpdateDto), userId, quizResultUpdateDto.getIncorrectIds());
        return ResponseEntity.ok(dtoMapper.asQuizResultUpdateDto(quizSimpleResult));
    }

    @Override
    public ResponseEntity<List<QuizSummaryDto>> listQuizzes(
            @Nullable String name,
            @Nullable String ownerId,
            @Nullable ResourceAccessTypeDto accessType,
            @Nullable UUID workspaceId,
            @Nullable Float averageRating) {
        String userId = userIdentityService.getCurrentUserId();
        PermissionLevel permissionLevel = dtoMapper.fromResourceAccessTypeDto(accessType);
        float averageRatingValue = averageRating == null ? 0 : averageRating;
        List<Quiz> quizzes = quizService.searchQuizzes(userId, workspaceId, ownerId, name, permissionLevel, averageRatingValue);

        return ResponseEntity.ok(quizzes.stream()
                .map(dtoMapper::asQuizSummaryDto)
                .toList());
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkIfUserIsOwnerOfResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<Void> deleteQuiz(UUID quizId) {
        quizService.deleteQuiz(quizId);
        return new ResponseEntity<>(OK);
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkUserPermissionToViewResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<List<QuizBestResultDto>> getTopResultsByQuizId(UUID quizId, Integer numberOfTopResults) {
        List<QuizUserResult> topQuizResults = quizService.getTopQuizResults(quizId, numberOfTopResults);
        return ResponseEntity.ok(topQuizResults.stream()
                .map(dtoMapper::toQuizBestResultDto)
                .toList());
    }

    @Override
    @PreAuthorize("""
                    @permissionAccessService.checkIfUserIsOwnerOfResource(#quizId, 'QUIZ') or
                    @userIdentityService.isCurrentUserAdmin()
            """)
    public ResponseEntity<QuizDetailsDto> updateQuizDetailsById(UUID quizId, QuizCreationDto quizCreationDto) {
        String userId = userIdentityService.getCurrentUserId();
        Quiz quiz = quizService.updateQuizDetailsById(quizId, quizCreationDto, userId);
        return ResponseEntity.ok(dtoMapper.asQuizDetailsDto(quiz));
    }

    //todo increment numberOfQuestions when adding questions
}