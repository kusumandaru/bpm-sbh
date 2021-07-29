package com.sbh.bpm.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.sbh.bpm.exception.BadRequestException;
import org.camunda.bpm.engine.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtUtil {

    @Autowired
    private IdentityService identityService;

    @Value(value = "${JWT.secret}")
    private String secret;

    @Value(value = "${JWT.expiryDuration}")
    private int jwtExpirationInMs;

    public String generateToken(String username, String password) {
        if(isAuthenticated(username, password)) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(secret);
                // TODO: create token with groupIds and tenantIds
                return JWT.create()
                        .withClaim("username", username)
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                        .sign(algorithm);
            } catch (Exception e) {
                throw new BadRequestException("Error create jwt token");
            }
        }

        throw new JWTCreationException("Error create jwt token", null);
    }

    private boolean isAuthenticated(String userName, String password) {
        return identityService.checkPassword(userName, password);
    }
}
