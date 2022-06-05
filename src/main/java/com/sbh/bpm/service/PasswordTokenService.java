package com.sbh.bpm.service;

import java.util.Calendar;
import java.util.UUID;

import com.sbh.bpm.model.PasswordToken;
import com.sbh.bpm.repository.PasswordTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordTokenService implements IPasswordTokenService {
  @Autowired
  private PasswordTokenRepository repository;

  private static final int EXPIRATION = 60 * 24;

  @Override
  public PasswordToken GenerateTokenByUserId(String userId) {
    String randomToken = UUID.randomUUID().toString();
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, EXPIRATION);
    PasswordToken token = new PasswordToken(userId, randomToken, cal.getTime());
    return repository.save(token);
  }

  @Override
  public String ValidatePasswordResetToken(String token) {
    final PasswordToken passToken = repository.findByToken(token);

    return !isTokenFound(passToken) ? "invalidToken"
            : isTokenExpired(passToken) ? "expired"
            : null;
  }

  private boolean isTokenFound(PasswordToken passToken) {
      return passToken != null;
  }
  private boolean isTokenExpired(PasswordToken passToken) {
    final Calendar cal = Calendar.getInstance();
    return passToken.getExpireDate().before(cal.getTime());
}
}
