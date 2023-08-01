package com.pixelthump.quizxelservice.service.model;
import lombok.Data;

@Data
public class SeshInfo {

    private String seshType;
    private String seshCode;

    public SeshInfo() {

        this.seshType = "quizxel";
    }

    public SeshInfo(String seshType, String seshCode) {

        this.seshType = seshType;
        this.seshCode = seshCode;
    }
}
