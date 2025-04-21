package com.boardgame.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private final LocalDateTime timeStamp;
    private String message;
    private int httpStatus;

    public ErrorResponse(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

}