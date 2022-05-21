package com.sbh.bpm.controller;

import java.time.Instant;

import javax.servlet.ServletException;
import javax.validation.Valid;

import com.sbh.bpm.payload.AuthRequest;
import com.sbh.bpm.payload.AuthResponse;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.security.JwtUtil;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private IdentityService identityService;

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<AuthResponse> credentials(@RequestBody AuthRequest loginRequest) {
        AuthResponse response = new AuthResponse();
        try {
            response = jwtUtil.generateToken(loginRequest.getEmail(), loginRequest.getPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) throws ServletException {
        String tenantName = registerRequest.getTenantName().replaceAll(" ", "-");
        String tenantId = tenantName.substring(0, Math.min(30, tenantName.length())).toLowerCase() + "-" + String.valueOf(Instant.now().toEpochMilli());

        UserEntity user = new UserEntity();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        String userId = String.valueOf(Instant.now().toEpochMilli());
        user.setId(userId);

        identityService.saveUser(user);
        identityService.createMembership(user.getId(), "superuser");

        TenantEntity tenant = new TenantEntity();
        tenant.setName(registerRequest.getTenantName());
        tenant.setId(tenantId);
        identityService.saveTenant(tenant);

        identityService.createTenantUserMembership(tenantId, user.getId());
        
        AuthResponse response = jwtUtil.generateToken(user.getEmail(), registerRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}
