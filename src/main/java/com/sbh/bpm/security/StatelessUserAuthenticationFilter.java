package com.sbh.bpm.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;

public class StatelessUserAuthenticationFilter implements Filter {
    public static final String AUTHENTICATION_PROVIDER_PARAM = "authentication-provider";
    public static final String JWT_SECRET = "jwt-secret";
    public static final String JWT_VALIDATOR = "jwt-validator";
    private static String jwtSecret;
    private static String jwtValidator;
    private static Class<?> jwtValidatorClass;
    protected AuthenticationProvider authenticationProvider;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String authenticationProviderClassName = filterConfig.getInitParameter(AUTHENTICATION_PROVIDER_PARAM);

        if (jwtSecret == null){
            jwtSecret = filterConfig.getInitParameter(JWT_SECRET);
        }

        if (jwtValidator == null){
            jwtValidator = filterConfig.getInitParameter(JWT_VALIDATOR);
        }

        try {
            Class<?> authenticationProviderClass = Class.forName(authenticationProviderClassName);
            authenticationProvider = (AuthenticationProvider) authenticationProviderClass.getDeclaredConstructor().newInstance();

        } catch (ClassNotFoundException e) {
            throw new ServletException("Cannot instantiate authentication filter: authentication provider not found", e);
        } catch (InstantiationException e) {
            throw new ServletException("Cannot instantiate authentication filter: cannot instantiate authentication provider", e);
        } catch (IllegalAccessException e) {
            throw new ServletException("Cannot instantiate authentication filter: constructor not accessible", e);
        } catch (ClassCastException e) {
            throw new ServletException("Cannot instantiate authentication filter: authentication provider does not implement interface " +
                    AuthenticationProvider.class.getName(), e);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }

        try {
            jwtValidatorClass = getClass().getClassLoader().loadClass(jwtValidator);
        } catch (ClassNotFoundException e) {
            throw new ServletException("Could not load Jwt Validator Class: ${all.getLocalizedMessage()}");
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        ProcessEngine engine = BpmPlatform.getDefaultProcessEngine();

        if (engine == null) {
            engine = ProcessEngines.getDefaultProcessEngine(false);
        }

        AuthenticationResult authenticationResult = authenticationProvider.extractAuthenticatedUser(req, engine, jwtValidatorClass, jwtSecret);

        if (authenticationResult.isAuthenticated()) {
            try {
                String authenticatedUser = authenticationResult.getAuthenticatedUser();

                List<String> groupIds = authenticationResult.getGroups() == null ? Arrays.asList() : authenticationResult.getGroups();
                List<String> tenantIds = authenticationResult.getTenants() == null ? Arrays.asList() : authenticationResult.getTenants();

                setAuthenticatedUser(engine, authenticatedUser, groupIds, tenantIds);

                chain.doFilter(request, response);

            } finally {
                clearAuthentication(engine);
            }
        } else {
            resp.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
            authenticationProvider.augmentResponseByAuthenticationChallenge(resp, engine);
        }

    }

    @Override
    public void destroy() {

    }

    protected void setAuthenticatedUser(ProcessEngine engine, String userId, List<String> groupIds, List<String> tenantIds) {
        engine.getIdentityService().setAuthentication(userId, groupIds, tenantIds);
    }

    private void clearAuthentication(ProcessEngine engine) {
        engine.getIdentityService().clearAuthentication();
    }
}
