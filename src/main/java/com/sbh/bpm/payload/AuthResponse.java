package com.sbh.bpm.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserData userData;

    public AuthResponse(String accessToken, String refreshToken, String name, String email, List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        UserData userData = new UserData();
        userData.setFullName(name);
        userData.setEmail(email);
        userData.setRoles(roles);
        this.userData = userData;
    }
}
