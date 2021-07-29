package com.sbh.bpm.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.camunda.bpm.engine.ProcessEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    @Override
    public AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine, Class<?> jwtValidator, String jwtSecret) {
        String jwt = getJwtFromRequest(request);

        try{
            AbstractValidator validator = (AbstractValidator) jwtValidator.newInstance();
            AuthenticationResult validationResult = validator.validateToken(jwt, jwtSecret);
            if (validationResult.isAuthenticated()) {
                String username = validationResult.getAuthenticatedUser();
                AuthenticationResult authenticationResult = new AuthenticationResult(username, true, null, null);
                return authenticationResult;
            }

            return AuthenticationResult.unsuccessful();
        } catch(JWTVerificationException e){
            // @TODO Add better Exception handling for JWT Validator class loading
            LOGGER.error("Could not load Jwt Validator Class: " + e.getLocalizedMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return AuthenticationResult.unsuccessful();
        }
    }

    @Override
    public void augmentResponseByAuthenticationChallenge(HttpServletResponse response, ProcessEngine engine) {
            LOGGER.error(engine.getName());
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}