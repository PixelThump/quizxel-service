package com.pixelthump.quizxelservice.service.model.messaging;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagingQuestion<T> {

    private String questionPack;
    private String questionText;
    private String type;
    private T questionAnswer;
}
