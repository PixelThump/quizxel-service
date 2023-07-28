package com.pixelthump.quizxelservice.sesh.model.state.question;
public interface Question {

    String getQuestionText();

    String getQuestionAnswer();

    QuestionType getType();
}
