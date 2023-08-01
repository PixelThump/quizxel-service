package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.repository.model.State;

public interface SeshFactory {

    State createSesh(String seshCode);
}
