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
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
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

    @Autowired
    private PlatformTransactionManager transactionManager;

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

        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        UserEntity user = new UserEntity();

        try {
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
    
            User u = userService.findById(user.getId());
            mailerService.SendRegisterEmail(u);
        } catch(Exception ex) {
            transactionManager.rollback(transactionStatus);
      
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

        TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        UserEntity user = new UserEntity();
        
        try {
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            String userId = String.valueOf(Instant.now().toEpochMilli());
            user.setId(userId);

            identityService.saveUser(user);
            identityService.createMembership(user.getId(), "user");

            identityService.createTenantUserMembership(registerRequest.getTenantId(), user.getId());

            User u = userService.findById(user.getId());
            u.setTenantOwner(false);
            u = userService.Save(u);

            mailerService.SendRegisterEmail(u);
        } catch(Exception ex) {
            transactionManager.rollback(transactionStatus);
      
            AuthResponse response = new AuthResponse();
            response.setError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        transactionManager.commit(transactionStatus);
        AuthResponse response = jwtUtil.generateToken(user.getEmail(), registerRequest.getPassword());
        return ResponseEntity.ok(response);
    }
}
