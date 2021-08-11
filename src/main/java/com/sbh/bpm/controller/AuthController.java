package com.sbh.bpm.controller;

import javax.servlet.ServletException;
import javax.validation.Valid;

import com.sbh.bpm.payload.AuthRequest;
import com.sbh.bpm.payload.AuthResponse;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.security.JwtUtil;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
        AuthResponse response = jwtUtil.generateToken(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) throws ServletException {
        UserEntity user = new UserEntity();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setId(registerRequest.getEmail());
        user.setFirstName(registerRequest.getName());

        identityService.saveUser(user);
        identityService.createMembership(user.getId(), "user");
        
        AuthResponse response = jwtUtil.generateToken(registerRequest.getEmail(), registerRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}
