package com.sbh.bpm.config;

import javax.servlet.Filter;

import com.sbh.bpm.security.JwtValidator;
import com.sbh.bpm.security.StatelessUserAuthenticationFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class CamundaConfig {

    @Value(value = "${JWT.secret}")
    private String jwtSecret;

    @Value(value = "${JWT.expiryDuration}")
    private Integer jwtExpiry;

    @Bean
    public AuthorizationConfig authorizationConfiguration() {
        return new AuthorizationConfig();
    }

    @Bean
    public FilterRegistrationBean<Filter> statelessUserAuthenticationFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<Filter>();
        registration.setName("camunda-jwt-filter");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        registration.addInitParameter("authentication-provider", "com.sbh.bpm.security.JwtAuthenticationProvider");
        registration.addInitParameter("jwt-secret", jwtSecret);
        registration.addInitParameter("jwt-expiry", jwtExpiry.toString());
        registration.addInitParameter("jwt-validator", JwtValidator.class.getName());
        registration.addUrlPatterns("/engine-rest/*");
        registration.setFilter(new StatelessUserAuthenticationFilter());
        return registration;
    }
}
