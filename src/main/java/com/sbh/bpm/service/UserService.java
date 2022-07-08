package com.sbh.bpm.service;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import com.sbh.bpm.model.Group;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.repository.GroupRepository;
import com.sbh.bpm.repository.UserRepository;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Service
@Transactional
public class UserService implements IUserService{

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private IdentityService identityService;

  @Autowired
  private IMailerService mailerService;

  @Autowired
  private PlatformTransactionManager transactionManager;


  @Override
  public User GetUserFromAuthorization(String authorization) {
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

    User user = userRepository.findById(sub).get();
    return user;
  }

  @Override
  public UserDetail GetCompleteUserFromAuthorization(String authorization) {
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

    return GetUserDetailFromId(sub);
  }

  @Override
  public UserDetail GetUserDetailFromId(String id) {
    User user = userRepository.findById(id).get();
    Tenant tenant = TenantFromUser(user);
    Group group = GroupFromUser(user);

    UserDetail userDetail = UserDetail.CreateFromUser(user, tenant, group);
    return userDetail;
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

  @Override
  public User Save(User user) {
    return userRepository.save(user);
  }

  @Override
  public User findById(String userId) {
    return userRepository.findById(userId).get();
  }

  @Override
  public List<User> findByTenantId(String tenantId) {
    return userRepository.findByTenantId(tenantId);
  }

  @Override
  public List<User> findAll() {
    return (List<User>) userRepository.findAll();
  }

  @Override
  public List<UserDetail> findAllDetail() {
    List<User> users = findAll();
    List<UserDetail> userDetails = users.stream().map(user -> GetUserDetailFromId(user.getId())).collect(Collectors.toList());

    return userDetails;
  }

  @Override
  public UserDetail findByIdDetail(String userId) {
    User user = findById(userId);
    return GetUserDetailFromId(user.getId());
  }

  @Override
  public User FindByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Long Count() {
    return userRepository.count();
  }

  @Override
  public Long CountByTenantId(String tenantId) {
    return userRepository.countByTenantId(tenantId);
  }

  @Override
  public UserDetail FindByToken(String token) {
    User user = userRepository.findByToken(token);
    return GetUserDetailFromId(user.getId());
  }

  @Override
  public User UpdateUser(User user, UserDetail u) throws Exception {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

    try {
      user.setActive(u.getActive());
      user.setEmail(u.getEmail());
      user.setFirstName(u.getFirstName());
      user.setLastName(u.getLastName());
      user.setTenantOwner(u.getTenantOwner());
      user = Save(user);

      com.sbh.bpm.model.Group groupUser = GroupFromUser(user);
      if (groupUser != null && !groupUser.getId().equals(u.getGroupId())) {
        identityService.deleteMembership(user.getId(), groupUser.getId());
        identityService.createMembership(user.getId(), u.getGroupId());
      }

      Tenant tenantUser = TenantFromUser(user);
      if (tenantUser != null && !tenantUser.getId().equals(u.getTenantId())) {
        identityService.deleteTenantUserMembership(tenantUser.getId(), user.getId());
        identityService.createTenantUserMembership(u.getTenantId(), user.getId());
      }

      return user;
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      throw ex;
    }
  }

  @Override
  public User UpdateMember(User user, UserDetail u) throws Exception {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

    try {
      user.setActive(u.getActive());
      user.setEmail(u.getEmail());
      user.setFirstName(u.getFirstName());
      user.setLastName(u.getLastName());
      user.setTenantOwner(u.getTenantOwner());
      user = Save(user);
  
      com.sbh.bpm.model.Group groupUser = GroupFromUser(user);
      if (groupUser != null && !groupUser.getId().equals(u.getGroupId())) {
        identityService.deleteMembership(user.getId(), groupUser.getId());
        identityService.createMembership(user.getId(), u.getGroupId());
      }

      return user;
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      throw ex;
    }
  }

  @Override
  public User RegisterUser(RegisterRequest registerRequest, String tenantId) throws Exception {
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

      User u = findById(user.getId());
      mailerService.SendRegisterEmail(u);

      return u;
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      throw ex;
    }
  }

  @Override
  public User InvitationUser(RegisterRequest registerRequest) throws Exception {
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

      User u = findById(user.getId());
      u.setTenantOwner(false);
      u = Save(u);

      mailerService.SendRegisterEmail(u);

      return u;
    } catch (Exception ex) {
      transactionManager.rollback(transactionStatus);

      throw ex;
    }
  }
}
