package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.service.model.Sesh;

public interface SeshFactory {

    Sesh createSesh(String seshCode);
}
