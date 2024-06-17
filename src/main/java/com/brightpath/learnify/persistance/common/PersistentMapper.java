package com.brightpath.learnify.persistance.common;

import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizSimpleResult;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.question.QuestionEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import com.brightpath.learnify.persistance.user.UserEntity;
import com.brightpath.learnify.persistance.workspace.WorkspaceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistentMapper {

    public Workspace asWorkspace(WorkspaceEntity entity) {
        return new Workspace(entity.getId(), entity.getDisplayName());
    }

    public User asUser(UserEntity entity) {
        return new User(entity.getId(), entity.getDisplayName(), entity.getEmail());
    }

    public Note asNote(NoteEntity entity) {
        return new Note(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                asWorkspace(entity.getWorkspace()),
                asUser(entity.getOwner()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public QuizSimpleResult asBestSimpleResult(QuizEntity entity) {
        if (entity.getBestNumberOfIncorrect() == null || entity.getBestNumberOfCorrect() == null) {
            return null;
        }
        return new QuizSimpleResult(entity.getBestNumberOfIncorrect(), entity.getBestNumberOfCorrect());
    }

    public QuizSimpleResult asLastSimpleResult(QuizEntity entity) {
        if (entity.getLastNumberOfIncorrect() == null || entity.getLastNumberOfCorrect() == null) {
            return null;
        }
        return new QuizSimpleResult(entity.getLastNumberOfIncorrect(), entity.getLastNumberOfCorrect());
    }

    public Quiz asQuiz(QuizEntity entity) {
        return new Quiz(
                entity.getId(),
                asWorkspace(entity.getWorkspace()),
                entity.getTitle(),
                entity.getDescription(),
                entity.getNumberOfQuestions(),
                asLastSimpleResult(entity),
                asBestSimpleResult(entity),
                asUser(entity.getAuthor()),
                entity.getLastTryDate(),
                entity.getCreatedAt()
        );
    }

    public Question asQuestion(QuestionEntity entity) {
        return new Question(entity.getId(),
                entity.getQuestion(),
                entity.getType(),
                entity.getQuizId(),
                entity.getWeight(),
                entity.getChoices(),
                entity.getFeedback(),
                entity.getOtherProperties());
    }

    public List<Question> asQuestions(List<QuestionEntity> savedEntities) {
        return savedEntities.stream()
                .map(this::asQuestion)
                .toList();
    }

    public QuizSimpleResult asQuizSimpleResult(Integer correct, Integer incorrect) {
        if (correct == null || incorrect == null) {
            return null;
        }
        return new QuizSimpleResult(incorrect, correct);
    }
}
