package com.sbh.bpm.controller;

import java.time.Instant;

import javax.servlet.ServletException;
import javax.validation.Valid;

import com.sbh.bpm.model.User;
import com.sbh.bpm.payload.AuthRequest;
import com.sbh.bpm.payload.AuthResponse;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.security.JwtUtil;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IUserService;

import org.camunda.bpm.engine.IdentityService;
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

    @Autowired
    private IMailerService mailerService;

    @Autowired
    private IUserService userService;

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
        User existingUser = userService.FindByEmail(registerRequest.getEmail());
        if (existingUser != null) {
            AuthResponse response = new AuthResponse();
            response.setError("Email already taken");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String tenantName = registerRequest.getTenantName().replaceAll(" ", "-");
        String tenantId = tenantName.substring(0, Math.min(30, tenantName.length())).toLowerCase() + "-" + String.valueOf(Instant.now().toEpochMilli());
        User user = new User();
        try {
            user = userService.RegisterUser(registerRequest, tenantId);
        } catch(Exception ex) {
            AuthResponse response = new AuthResponse();
            response.setError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        AuthResponse response = jwtUtil.generateToken(user.getEmail(), registerRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invitation")
    public ResponseEntity<AuthResponse> invitation(@Valid @RequestBody RegisterRequest registerRequest) throws ServletException {
        User existingUser = userService.FindByEmail(registerRequest.getEmail());
        if (existingUser != null) {
            AuthResponse response = new AuthResponse();
            response.setError("Email already taken");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        User user = new User();
        try {
            user = userService.InvitationUser(registerRequest);
        } catch(Exception ex) {
            AuthResponse response = new AuthResponse();
            response.setError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        AuthResponse response = jwtUtil.generateToken(user.getEmail(), registerRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}
