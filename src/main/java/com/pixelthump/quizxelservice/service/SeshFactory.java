package com.pixelthump.quizxelservice.service;
import com.pixelthump.quizxelservice.sesh.Sesh;

public interface SeshFactory {

    Sesh createSesh(String seshCode);
}
