package com.sbh.bpm.service;

import com.sbh.bpm.model.PasswordToken;

public interface IPasswordTokenService {
  PasswordToken GenerateTokenByUserId(String userId);
  String ValidatePasswordResetToken(String token);
}
