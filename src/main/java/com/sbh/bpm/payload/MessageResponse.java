package com.sbh.bpm.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {
    private int code;
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
