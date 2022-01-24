package com.sbh.bpm.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JwtValidator extends AbstractValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtValidator.class);


    @Override
    public AuthenticationResult validateToken(String encodedCredentials, String jwtSecret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .acceptNotBefore(new Date().getTime())
                    .build();
            DecodedJWT jwt = verifier.verify(encodedCredentials);

            String username = jwt.getClaim("username").asString();
            List<String> groupIds = jwt.getClaim("groupIds").asList(String.class);
            List<String> tenantIds = jwt.getClaim("tenantIds").asList(String.class);

            if (username.isEmpty()) {
                LOGGER.error("BAD JWT: Missing username");
                throw new com.auth0.jwt.exceptions.JWTVerificationException("Missing username");
            }

            return new AuthenticationResult(username, true, groupIds, tenantIds);

        } catch (JWTVerificationException ex) {
            LOGGER.error("Verification error: " + ex.getLocalizedMessage());
            throw ex;
        }
    }
}
