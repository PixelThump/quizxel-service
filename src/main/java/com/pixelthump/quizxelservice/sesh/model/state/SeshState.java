package com.pixelthump.quizxelservice.sesh.model.state;
import com.pixelthump.quizxelservice.sesh.model.Player;
import com.pixelthump.quizxelservice.sesh.model.SeshStage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeshState {

    private List<Player> players;
    private String seshCode;
    private SeshStage currentStage;

}
