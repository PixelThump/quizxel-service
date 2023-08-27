package com.pixelthump.seshtypelib.service;
import com.pixelthump.seshtypelib.service.model.State;

public interface StateFactory {

    State createSeshTypeState(String seshCode);
}
