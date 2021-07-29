package com.sbh.bpm.security;

import org.camunda.bpm.engine.ProcessEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationProvider {

    /**
     * Checks the request for authentication. May not return null, but always an {@link AuthenticationResult} that indicates, whether
     * authentication was successful, and, if true, always provides the authenticated user, and optionally group IDs and tenant IDs.
     *
     * @param request       the request to authenticate
     * @param engine        the process engine the request addresses.
     * @param jwtValidator  the fully qualified class name that extends AbstractValidatorJwt, that will be used to validate the JWT.
     * @param jwtSecret     the file path of the location of the secret used to decode/validate the JWT.  May be null if secret is pulled from another location.
     */
    AuthenticationResult extractAuthenticatedUser(HttpServletRequest request, ProcessEngine engine, Class<?> jwtValidator, String jwtSecret);

    /**
     * <p>
     * Callback to add an authentication challenge to the response to the client. Called in case of unsuccessful authentication.
     * </p>
     *
     * <p>
     * For example, a Http Basic auth implementation may set the WWW-Authenticate header to <code>Basic realm="engine name"</code>.
     * </p>
     *
     * @param response the response to augment
     * @param engine   the process engine the request addressed. May be considered as an authentication realm to create a specific authentication
     *                 challenge
     */
    void augmentResponseByAuthenticationChallenge(HttpServletResponse response, ProcessEngine engine);
}
