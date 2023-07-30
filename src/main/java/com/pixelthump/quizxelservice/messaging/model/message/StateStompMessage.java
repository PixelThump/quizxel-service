package com.pixelthump.quizxelservice.messaging.model.message;
import com.pixelthump.quizxelservice.sesh.model.SeshState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class StateStompMessage implements StompMessage {

    SeshState state;
}
