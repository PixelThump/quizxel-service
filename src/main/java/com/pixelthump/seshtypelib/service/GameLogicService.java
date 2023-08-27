package com.pixelthump.seshtypelib.service;
import com.pixelthump.seshtypelib.service.model.State;

public interface GameLogicService {

    State processQueue(State state);
}
