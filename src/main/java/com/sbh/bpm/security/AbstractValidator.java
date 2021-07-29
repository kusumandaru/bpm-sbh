package com.sbh.bpm.security;

public abstract class AbstractValidator {
    public abstract AuthenticationResult validateToken(String encodedCredentials, String jwtSecret);
}
