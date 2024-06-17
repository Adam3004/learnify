package com.brightpath.learnify.persistance.binding;

import com.brightpath.learnify.persistance.note.NoteEntity;
import com.brightpath.learnify.persistance.quiz.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BindingRepository extends JpaRepository<BindingEntity, UUID>{

    @Query("SELECT b.note FROM BindingEntity b WHERE b.quiz.id = :quizId")
    List<NoteEntity> findAllBoundNotesByQuizId(UUID quizId);

    @Query("SELECT b.quiz FROM BindingEntity b WHERE b.note.id = :noteId")
    List<QuizEntity> findAllBoundQuizzesByNoteId(UUID noteId);
}
