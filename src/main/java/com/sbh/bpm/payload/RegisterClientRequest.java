package com.sbh.bpm.payload;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterClientRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private String firstName;
    private String lastName;
    private String tenantId;
    private String groupId;
    private Boolean owner;
}
