package com.sbh.bpm.service;

import com.sbh.bpm.model.Group;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;

import org.camunda.bpm.engine.identity.Tenant;

public interface IUserService {
  User GetUserFromAuthorization(String authorization);
  UserDetail GetCompleteUserFromAuthorization(String authorization);
  UserDetail GetUserDetailFromId(String id);
  Tenant TenantFromUser(User user);
  Group GroupFromUser(User user);
  User Save(User user);
}
