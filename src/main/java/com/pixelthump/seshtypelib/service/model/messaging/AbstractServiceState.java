package com.pixelthump.seshtypelib.service.model.messaging;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public abstract class AbstractServiceState {
    private String seshCode;
}
