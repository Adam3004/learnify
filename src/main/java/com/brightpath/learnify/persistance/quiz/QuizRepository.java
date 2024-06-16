package com.brightpath.learnify.persistance.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizRepository extends JpaRepository<QuizEntity, UUID> {
}
