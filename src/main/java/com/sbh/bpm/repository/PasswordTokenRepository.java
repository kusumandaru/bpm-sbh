package com.sbh.bpm.repository;


import com.sbh.bpm.model.PasswordToken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordToken, Integer> {
  PasswordToken findByToken(String token);
}
