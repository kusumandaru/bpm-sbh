package com.sbh.bpm.config;

import java.util.Arrays;

import com.google.common.base.Splitter;
import com.sbh.bpm.security.JwtAuthenticationEntryPoint;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint entryPoint;

    @Value("${cors.urls}")
    String corsUrls;

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

    @Bean
    FilterRegistrationBean<CorsFilter> corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Splitter.on(",").splitToList(corsUrls));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
        return bean;
    }
}
