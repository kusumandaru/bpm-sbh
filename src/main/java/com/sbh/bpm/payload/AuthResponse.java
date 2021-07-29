package com.sbh.bpm.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String status;
    private String message;

    public AuthResponse(String token, String status, String message) {
        this.token = token;
        this.status = status;
        this.message = message;
    }
}
