package com.sbh.bpm.config;

import com.sbh.bpm.security.JwtValidator;
import com.sbh.bpm.security.StatelessUserAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

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
    public FilterRegistrationBean statelessUserAuthenticationFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("camunda-jwt-filter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addInitParameter("authentication-provider", "com.sbh.bpm.security.JwtAuthenticationProvider");
        registration.addInitParameter("jwt-secret", jwtSecret);
        registration.addInitParameter("jwt-expiry", jwtExpiry.toString());
        registration.addInitParameter("jwt-validator", JwtValidator.class.getName());
        registration.addUrlPatterns("/rest/*", "/engine-rest/*");
        registration.setFilter(new StatelessUserAuthenticationFilter());
        return registration;
    }
}