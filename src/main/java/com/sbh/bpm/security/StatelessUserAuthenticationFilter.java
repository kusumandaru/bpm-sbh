package com.sbh.bpm.security;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class StatelessUserAuthenticationFilter implements Filter {
    public static final String AUTHENTICATION_PROVIDER_PARAM = "authentication-provider";
    public static final String JWT_SECRET = "jwt-secret";
    public static final String JWT_EXPIRY = "jwt-expiry";
    public static final String JWT_VALIDATOR = "jwt-validator";
    private static String jwtSecret;
    private static int jwtExpiry;
    private static String jwtValidator;
    private static Class<?> jwtValidatorClass;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatelessUserAuthenticationFilter.class);
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

        jwtExpiry = Integer.parseInt(filterConfig.getInitParameter(JWT_EXPIRY));

        try {
            Class<?> authenticationProviderClass = Class.forName(authenticationProviderClassName);
            authenticationProvider = (AuthenticationProvider) authenticationProviderClass.newInstance();

        } catch (ClassNotFoundException e) {
            throw new ServletException("Cannot instantiate authentication filter: authentication provider not found", e);
        } catch (InstantiationException e) {
            throw new ServletException("Cannot instantiate authentication filter: cannot instantiate authentication provider", e);
        } catch (IllegalAccessException e) {
            throw new ServletException("Cannot instantiate authentication filter: constructor not accessible", e);
        } catch (ClassCastException e) {
            throw new ServletException("Cannot instantiate authentication filter: authentication provider does not implement interface " +
                    AuthenticationProvider.class.getName(), e);
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

                // @TODO Review if null or empty array should be sent into Groups and Tenants when JWT does not have these claims
                List<String> groupIds = Arrays.asList();
                List<String> tenantIds = Arrays.asList();

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
