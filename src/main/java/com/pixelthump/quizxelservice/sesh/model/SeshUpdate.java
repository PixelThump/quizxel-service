package com.pixelthump.quizxelservice.sesh.model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeshUpdate {

    private StateWrapper host;
    private StateWrapper controller;

}