package com.brightpath.learnify.quiz.model.quiz.question;

import com.brightpath.learnify.model.QuestionDto;

public class SingleQuestion extends Question {
    private final int answer;

    public SingleQuestion(QuestionDto questionDto) {
        super(questionDto.getQuestion(), questionDto.getType(), questionDto.getQuizId(), questionDto.getWeight(),
                questionDto.getChoices(), questionDto.getFeedback());
        this.answer = Integer.parseInt(questionDto.getOtherProperties().get("answer"));
    }

    @Override
    public <T> <T> getAnswer() {
        return answer;
    }
}
