package com.sbh.bpm.config;

import javax.servlet.Filter;

import com.sbh.bpm.security.JwtValidator;
import com.sbh.bpm.security.StatelessUserAuthenticationFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamundaConfig {

    @Value("${JWT.secret}")
    private String jwtSecret;

    @Value("${JWT.expiryDuration}")
    private Integer jwtExpiry;

    @Bean
    public AuthorizationConfig authorizationConfiguration() {
        return new AuthorizationConfig();
    }

    @Bean
    public FilterRegistrationBean<Filter> statelessUserAuthenticationFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
        registration.setName("camunda-jwt-filter");
        registration.addInitParameter("authentication-provider", "com.sbh.bpm.security.JwtAuthenticationProvider");
        registration.addInitParameter("jwt-secret", jwtSecret);
        registration.addInitParameter("jwt-expiry", jwtExpiry.toString());
        registration.addInitParameter("jwt-validator", JwtValidator.class.getName());
        registration.addUrlPatterns("/rest/*", "/client/*");
        registration.setFilter(new StatelessUserAuthenticationFilter());
        return registration;
    }
}
