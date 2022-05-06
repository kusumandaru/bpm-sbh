package com.sbh.bpm.service;

import java.util.Base64;

import com.sbh.bpm.model.Group;
import com.sbh.bpm.repository.GroupRepository;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements IUserService{

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private GroupRepository groupRepository;

  @Override
  public User GetUserFromAuthorization(String authorization) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    IdentityService identityService = processEngine.getIdentityService();

    String[] token = authorization.split(" ");
    String[] chunks = token[1].split("\\.");
    Base64.Decoder decoder = Base64.getUrlDecoder();
    String payload = new String(decoder.decode(chunks[1]));
    String sub = "";
    try {
      JSONObject payloadJson = new JSONObject(payload);
      sub = payloadJson.getString("sub");
    } catch (JSONException e) {
      logger.error(e.getMessage());
    }

    User user = identityService.createUserQuery().userId(sub).singleResult();

    return user;
  }

  @Override
  public Tenant TenantFromUser(User user) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    IdentityService identityService = processEngine.getIdentityService();

    Tenant tenant = identityService.createTenantQuery().userMember(user.getId()).singleResult();
    return tenant;
  }

  @Override
  public Group GroupFromUser(User user) {
    Group group = groupRepository.getGroupByUserId(user.getId());
    return group;
  }
}
