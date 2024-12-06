package com.brightpath.learnify.domain.quiz.result;

import java.time.OffsetDateTime;

public record QuizUserResult(String userName,
                             int percentage,
                             OffsetDateTime tryDate) {
}
