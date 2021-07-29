package com.sbh.bpm.security;

import java.util.List;

public class AuthenticationResult  {
    protected boolean isAuthenticated;
    protected String authenticatedUser;
    protected List<String> groups;
    protected List<String> tenants;

    public AuthenticationResult(String authenticatedUser, boolean isAuthenticated, List<String> groups, List<String> tenants) {
        this.authenticatedUser = authenticatedUser;
        this.isAuthenticated = isAuthenticated;
        this.groups = groups;
        this.tenants = tenants;
    }

    public AuthenticationResult(String authenticatedUser, boolean isAuthenticated) {
        this.authenticatedUser = authenticatedUser;
        this.isAuthenticated = isAuthenticated;
        this.groups = null;
        this.tenants = null;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public String getAuthenticatedUser() {
        return authenticatedUser;
    }

    public static AuthenticationResult successful(String userId) {
        return new AuthenticationResult(userId, true, null, null);
    }

    public static AuthenticationResult successful(String userId, List<String> groups, List<String> tenants) {
        return new AuthenticationResult(userId, true, groups, tenants);
    }

    public static AuthenticationResult unsuccessful() {
        return new AuthenticationResult(null, false, null, null);
    }

    public static AuthenticationResult unsuccessful(String userId) {
        return new AuthenticationResult(userId, false, null, null);
    }
}
