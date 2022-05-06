package com.sbh.bpm.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.sbh.bpm.exception.BadRequestException;
import com.sbh.bpm.payload.AuthResponse;
import com.sbh.bpm.service.IUserService;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {

    @Autowired
    private IdentityService identityService;

    @Autowired
    private IUserService userService;

    @Value(value = "${JWT.secret}")
    private String secret;

    @Value(value = "${JWT.expiryDuration}")
    private int jwtExpirationInMs;

    public AuthResponse generateToken(String username, String password) {
        if(isAuthenticated(username, password)) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(secret);
                
                List<Group> groups = identityService.createGroupQuery().groupMember(username).list();
                List<String> groupIds = groups.stream().map(Group::getId).collect(Collectors.toList());
                User user = identityService.createUserQuery().userId(username).singleResult();
                String name = user.getFirstName() + " " + user.getLastName();
                Tenant tnt = userService.TenantFromUser(user);
                String tenantName = "";
                if (tnt != null) {
                    tenantName = tnt.getName();
                }
                // TODO: create token with groupIds and tenantIds
                String accessToken =  JWT.create()
                        .withSubject(user.getId())
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                        .sign(algorithm);
                return new AuthResponse(accessToken, "", name, username, tenantName, groupIds);
            } catch (Exception e) {
                throw new BadRequestException("Error create jwt token");
            }
        }

        throw new JWTCreationException("Error create jwt token", null);
    }

    private boolean isAuthenticated(String userName, String password) {
        return identityService.checkPassword(userName, password);
    }
}
