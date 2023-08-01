package com.pixelthump.quizxelservice.rest.model;
import com.pixelthump.quizxelservice.service.model.Command;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class QuizxelCommandWrapper {

    Command command;
}
