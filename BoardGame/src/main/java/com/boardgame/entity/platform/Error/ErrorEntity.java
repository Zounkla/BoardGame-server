package com.boardgame.entity.platform.Error;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorEntity {
    private final LocalDateTime timeStamp;
    private String message;
    private int httpStatus;

    public ErrorEntity(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}