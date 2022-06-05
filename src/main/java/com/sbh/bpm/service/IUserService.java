package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Group;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;

import org.camunda.bpm.engine.identity.Tenant;

public interface IUserService {
  User GetUserFromAuthorization(String authorization);
  User FindByEmail(String email);
  UserDetail GetCompleteUserFromAuthorization(String authorization);
  UserDetail GetUserDetailFromId(String id);
  Tenant TenantFromUser(User user);
  Group GroupFromUser(User user);
  User Save(User user);
  User findById(String userId);
  UserDetail findByIdDetail(String userId);
  List<User> findByTenantId(String tenantId);
  List<User> findAll();
  List<UserDetail> findAllDetail();
  Long Count();
  Long CountByTenantId(String tenantId);
  UserDetail FindByToken(String token);
}
