package com.pixelthump.quizxelservice.sesh.model.state.question;
import lombok.Data;

@Data
public class BuzzerQuestion implements Question {

    private String questionText;
    private String questionAnswer;
    private QuestionType type;

    public BuzzerQuestion(String questionText, String questionAnswer) {

        this.questionText = questionText;
        this.questionAnswer = questionAnswer;
        this.type = QuestionType.BUZZER;
    }
}
