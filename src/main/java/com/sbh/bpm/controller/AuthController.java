package com.sbh.bpm.controller;

import com.sbh.bpm.payload.ApiResponse;
import com.sbh.bpm.payload.AuthRequest;
import com.sbh.bpm.payload.AuthResponse;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.security.JwtUtil;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.validation.Valid;

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
        String token = jwtUtil.generateToken(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new AuthResponse(token, "200", "Sukses"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) throws ServletException {
        UserEntity user = new UserEntity();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setId(registerRequest.getEmail());
        user.setFirstName(registerRequest.getName());
        identityService.saveUser(user);
        return ResponseEntity.ok(new ApiResponse(true, "User successfully registered", HttpStatus.CREATED));
    }
}
