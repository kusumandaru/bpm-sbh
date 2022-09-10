package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Group;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.payload.RegisterRequest;

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
  List<User> findByGroupId(String groupId);
  List<User> findAll();
  List<UserDetail> findAllDetail();
  Long Count();
  Long CountByTenantId(String tenantId);
  UserDetail FindByToken(String token);
  User UpdateUser(User user, UserDetail u) throws Exception;
  User UpdateMember(User user, UserDetail u) throws Exception;
  User RegisterUser(RegisterRequest registerRequest, String tenantId) throws Exception;
  User InvitationUser(RegisterRequest registerRequest) throws Exception;
}
