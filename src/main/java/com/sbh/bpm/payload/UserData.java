package com.sbh.bpm.payload;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserData {
    private String email;
    private String fullName;
    private String avatar;
    private List<String> roles;

    public UserData() {
    }
}

