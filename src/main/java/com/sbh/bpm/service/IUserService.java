package com.sbh.bpm.service;

import com.sbh.bpm.model.Group;

import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;

public interface IUserService {
  User GetUserFromAuthorization(String authorization);
  Tenant TenantFromUser(User user);
  Group GroupFromUser(User user);
}
