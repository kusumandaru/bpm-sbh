package com.sbh.bpm.config;

import com.sbh.bpm.security.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint entryPoint;

    public SecurityConfig(JwtAuthenticationEntryPoint entryPoint) {
        this.entryPoint = entryPoint;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .requestMatchers().antMatchers("/engine-rest/**", "/auth/**", "/rest/**")
                .and()
                .csrf().ignoringAntMatchers("/engine-rest/**", "/auth/**", "/rest/**")
                .and()
                .exceptionHandling().authenticationEntryPoint(entryPoint);
    }
}
