package com.pixelthump.quizxelservice.service.model.question;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MessagingQuestion<T> {

    private String questionPack;
    private String questionText;
    private String type;
    private T questionAnswer;
}
