package com.sbh.bpm.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException, ServletException {
        Exception exception = (Exception) httpServletRequest.getAttribute("exception");
        RuntimeException runtimeException = (RuntimeException) exception;

        if (runtimeException != null) {
            int statusCode = AnnotationUtils.findAnnotation(runtimeException.getClass(), ResponseStatus.class).code().value();
            httpServletResponse.sendError(statusCode, runtimeException.getMessage());
            return;
        }

        if (exception != null) {
            LOGGER.error("Authentication failed", exception);
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        } else {
            LOGGER.error("Authentication failed", e);
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }
}
