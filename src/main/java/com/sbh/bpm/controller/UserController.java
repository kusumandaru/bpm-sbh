package com.sbh.bpm.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.sbh.bpm.model.PasswordToken;
import com.sbh.bpm.model.ProjectUser;
import com.sbh.bpm.model.ProjectVerificator;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.payload.RegisterClientRequest;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IPasswordTokenService;
import com.sbh.bpm.service.IProjectUserService;
import com.sbh.bpm.service.IProjectVerificatorService;
import com.sbh.bpm.service.ITenantService;
import com.sbh.bpm.service.IUserService;
import com.sbh.bpm.service.PasswordGenerator;
import com.sbh.bpm.service.PasswordValidator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.coobird.thumbnailator.Thumbnails;

@Path(value = "/user")
public class UserController extends GcsUtil{
  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private IUserService userService;

  @Autowired
  private ITenantService tenantService;

  @Autowired
  private IdentityService identityService;

  @Autowired
  private IProjectUserService projectUserService;

  @Autowired
  private IProjectVerificatorService projectVerificatorService;

  @Autowired
  private IPasswordTokenService passwordTokenService;

  @Autowired
  private IMailerService mailerService;

  @GET
  @Path(value = "/profile")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProfile(@HeaderParam("Authorization") String authorization
  ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    String json = new Gson().toJson(user);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/profile")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response SaveProfile(@HeaderParam("Authorization") String authorization, User u
  ) {
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    user.setActive(u.getActive());
    user.setEmail(u.getEmail());
    user.setFirstName(u.getFirstName());
    user.setLastName(u.getLastName());

    user = userService.Save(user);

    String json = new Gson().toJson(user);
    return Response.status(200).entity(json).build();
  }

  @PATCH
  @Path(value = "/profile/password")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response PasswordUpdate(
    @HeaderParam("Authorization") String authorization,
    @FormParam("new_password") String newPassword,
    @FormParam("current_password") String currentPassword) {
      if (newPassword.equals(currentPassword)) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "new password and recent password must be different");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User user = userService.GetUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }
      
      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
      IdentityService identityService = processEngine.getIdentityService();

      Boolean validPassword = identityService.checkPassword(user.getId(), currentPassword);
      if (!validPassword) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "recent password not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      if (!PasswordValidator.isValid(newPassword)) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "new password must be combination of Uppercase, lowercase, special character and minimum 8 digit");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      org.camunda.bpm.engine.identity.User updatedUser = identityService.createUserQuery().userId(user.getId()).singleResult();
      updatedUser.setPassword(newPassword);
      identityService.saveUser(updatedUser);

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }

  @POST
  @Path(value = "/profile/avatar")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response UploadProfileAvatar(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("avatar") InputStream avatar, 
    @FormDataParam("avatar") FormDataContentDisposition avatarFdcd
  ) { 
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    try {
      BufferedImage originalImage = ImageIO.read(avatar);
      BufferedImage thumbnail = Thumbnails.of(originalImage)
        .size(200, 200)
        .asBufferedImage();

      ByteArrayOutputStream os = new ByteArrayOutputStream();
      String filename = avatarFdcd.getFileName();
      String ext = FilenameUtils.getExtension(filename);
      ImageIO.write(thumbnail, ext, os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
      InputStream is = new ByteArrayInputStream(os.toByteArray());
      SaveAvatar(is, avatarFdcd, user);
    } catch (IOException e) {
      return Response.status(400, e.getMessage()).build();
    }


    return Response.ok().build();
  }

  @GET
  @Path(value = "/profile/avatar")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectAttachmentUrl(@HeaderParam("Authorization") String authorization
  ) {
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    if (user.getAvatarUrl() == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "avatar blank");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    String result;
    try {
      result = GetUrlGcs(user.getAvatarUrl());
    } catch (IOException e) {
      return Response.status(400).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result);

    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/users")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllUsers(
    @HeaderParam("Authorization") String authorization) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin", "verificator"));
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }
      
      List<UserDetail> users = userService.findAllDetail();

      String json = new Gson().toJson(users);
      return Response.status(200).entity(json).build();
    }
  
  @GET
  @Path(value = "/user_count")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CountAllUsers(
    @HeaderParam("Authorization") String authorization) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin", "verificator"));
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }
      
      Long count = userService.Count();

      String json = new Gson().toJson(count);
      return Response.status(200).entity(json).build();
    }

  @GET
  @Path(value = "/grouping_user")
  @Produces(MediaType.APPLICATION_JSON)
  public Response UserGrouping(
    @HeaderParam("Authorization") String authorization) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin", "verificator"));
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }
      
      Map<String, Long> dict = new TreeMap<>();
      List<com.sbh.bpm.model.Tenant> tenantList = tenantService.findAll();

      tenantList.stream().forEach(t -> {
        Long userCount = userService.CountByTenantId(t.getId());
        dict.put(t.getName(), userCount);
      });

      String json = new Gson().toJson(dict);
      return Response.status(200).entity(json).build();
    }
  
  @GET
  @Path(value = "/tenants/count")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CountAllTenant(
    @HeaderParam("Authorization") String authorization
    ) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      Long count = tenantService.Count();

      String json = new Gson().toJson(count);
      return Response.status(200).entity(json).build();
    }
    
  @POST
  @Path(value = "/users")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response UserCreation(
    @HeaderParam("Authorization") String authorization,
    RegisterClientRequest registerRequest) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin"));
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User existingUser = userService.FindByEmail(registerRequest.getEmail());
      if (existingUser != null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Email already taken");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      if (!PasswordValidator.isValid(registerRequest.getPassword())) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Password must be combination of Uppercase, lowercase, special character and minimum 8 digit");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      UserEntity userEntity = new UserEntity();
      userEntity.setEmail(registerRequest.getEmail());
      userEntity.setPassword(registerRequest.getPassword());
      userEntity.setFirstName(registerRequest.getFirstName());
      userEntity.setLastName(registerRequest.getLastName());
      String userId = String.valueOf(Instant.now().toEpochMilli());
      userEntity.setId(userId);

      identityService.saveUser(userEntity);
      identityService.createMembership(userEntity.getId(), registerRequest.getGroupId());

      if (registerRequest.getTenantId() != null) {
        identityService.createTenantUserMembership(registerRequest.getTenantId(), userEntity.getId());
      }
      
      User user = userService.findById(userEntity.getId());
      user.setTenantOwner(registerRequest.getOwner());
      user = userService.Save(user);

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }

  @GET
  @Path(value = "/users/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetUserById(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId
    ) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      UserDetail user = userService.findByIdDetail(userId);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected user not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }
      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
      IdentityService identityService = processEngine.getIdentityService();
      List<Group> groups = identityService.createGroupQuery().list();
      if (!StringUtils.isEmpty(userDetail.getGroupId()) && !groups.stream().anyMatch(group -> group.getId().equals(userDetail.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected group not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<Tenant> tenants = identityService.createTenantQuery().list();
      if (!StringUtils.isEmpty(userDetail.getTenantId()) && !tenants.stream().anyMatch(tenant -> tenant.getId().equals(userDetail.getTenantId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected tenant not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }
      
  @PATCH
  @Path(value = "/users/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response UserUpdate(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId,
    UserDetail u) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin"));
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User user = userService.findById(userId);
      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
      IdentityService identityService = processEngine.getIdentityService();
      List<Group> groups = identityService.createGroupQuery().list();
      if (!StringUtils.isEmpty(u.getGroupId()) && !groups.stream().anyMatch(group -> group.getId().equals(u.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected group not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<Tenant> tenants = identityService.createTenantQuery().list();
      if (!StringUtils.isEmpty(u.getTenantId()) && !tenants.stream().anyMatch(tenant -> tenant.getId().equals(u.getTenantId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected tenant not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User existingUser = userService.FindByEmail(user.getEmail());
      if (existingUser != null && !existingUser.getEmail().equals(user.getEmail())) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Email already taken");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      try {
      user = userService.UpdateUser(user, u);

    } catch(Exception ex) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", ex.getMessage());
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }

  @DELETE
  @Path(value = "/users/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response UserDelete(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin"));
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
      IdentityService identityService = processEngine.getIdentityService();

      UserDetail deletedUser = userService.GetUserDetailFromId(userId);
      if (deletedUser == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "deleted user not found");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      try {
        identityService.deleteMembership(deletedUser.getId(), deletedUser.getGroupId());
        if(deletedUser.getTenantId() != null) {
          identityService.deleteTenantUserMembership(deletedUser.getTenant().getId(), deletedUser.getId());
        }
        identityService.deleteUser(deletedUser.getId());
      } catch(Exception ex) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", ex.getMessage());
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      return Response.ok().build();
    }

  @GET
  @Path(value = "/members")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllMemberByTenant(
    @HeaderParam("Authorization") String authorization) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<User> users = userService.findByTenantId(userDetail.getTenant().getId());

      String json = new Gson().toJson(users);
      return Response.status(200).entity(json).build();
    }
  
  @GET
  @Path(value = "/members/count")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CountMemberByTenant(
    @HeaderParam("Authorization") String authorization) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      Long count = userService.CountByTenantId(userDetail.getTenant().getId());

      String json = new Gson().toJson(count);
      return Response.status(200).entity(json).build();
    }
    
  @POST
  @Path(value = "/members")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response MemberCreation(
    @HeaderParam("Authorization") String authorization,
    RegisterRequest registerRequest) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User existingUser = userService.FindByEmail(registerRequest.getEmail());
      if (existingUser != null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Email already taken");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      if (!PasswordValidator.isValid(registerRequest.getPassword())) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Password must be combination of Uppercase, lowercase, special character and minimum 8 digit");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      String password = PasswordGenerator.generateStrongPassword();
      UserEntity userEntity = new UserEntity();
      userEntity.setEmail(registerRequest.getEmail());
      userEntity.setPassword(password);
      userEntity.setFirstName(registerRequest.getFirstName());
      userEntity.setLastName(registerRequest.getLastName());
      String userId = String.valueOf(Instant.now().toEpochMilli());
      userEntity.setId(userId);

      identityService.saveUser(userEntity);
      identityService.createMembership(userEntity.getId(), "user");

      identityService.createTenantUserMembership(userDetail.getTenant().getId(), userEntity.getId());
      
      User user = userService.findById(userEntity.getId());
      user.setTenantOwner(false);
      user = userService.Save(user);
      
      mailerService.SendUserCreationEmail(user, password);

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }

  @PATCH
  @Path(value = "/members/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response MemberUpdate(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId,
    UserDetail u) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User user = userService.findById(userId);
      Tenant tenant = userService.TenantFromUser(user);
      if (user == null || !tenant.getId().equals(userDetail.getTenant().getId())) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected user not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<Group> groups = identityService.createGroupQuery().list();
      if (!StringUtils.isEmpty(u.getGroupId()) && !groups.stream().anyMatch(group -> group.getId().equals(u.getGroupId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected group not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User existingUser = userService.FindByEmail(user.getEmail());
      if (existingUser != null && !existingUser.getEmail().equals(user.getEmail())) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Email already taken");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      try {
        user = userService.UpdateMember(user, u);
      } catch(Exception ex) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", ex.getMessage());
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
    }

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }

  @DELETE
  @Path(value = "/members/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response MemberDelete(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      User user = userService.findById(userId);
      Tenant tenant = userService.TenantFromUser(user);
      if (user == null || !tenant.getId().equals(userDetail.getTenant().getId())) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected user not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      UserDetail deletedUser = userService.GetUserDetailFromId(userId);
      if (deletedUser == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "deleted user not found");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      try {
        identityService.deleteMembership(deletedUser.getId(), deletedUser.getGroupId());
        if(deletedUser.getTenantId() != null) {
          identityService.deleteTenantUserMembership(deletedUser.getTenant().getId(), deletedUser.getId());
        }
        identityService.deleteUser(deletedUser.getId());
      } catch(Exception ex) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", ex.getMessage());
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      return Response.ok().build();
    }

  @GET
  @Path(value = "/members/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMemberById(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId
    ) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      UserDetail user = userService.GetUserDetailFromId(userId);
      if (user == null || !user.getTenant().getId().equals(userDetail.getTenant().getId())) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "selected user not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }
    
  @GET
  @Path(value = "/tenant_project_users")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetTenantProjectUser(
    @HeaderParam("Authorization") String authorization
    ) {
      UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<ProjectUser> projectUsers = projectUserService.findByTenantId(user.getTenant().getId());

      String json = new Gson().toJson(projectUsers);
      return Response.status(200).entity(json).build();
    }

  @GET
  @Path(value = "/project_users_by_process_instance_id/{process_instance_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectUserByProcessInstanceID(
    @HeaderParam("Authorization") String authorization,
    @PathParam("process_instance_id") String processInstanceID
    ) {
      User user = userService.GetUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<ProjectUser> projectUsers = projectUserService.findByProcessInstanceID(processInstanceID);

      String json = new Gson().toJson(projectUsers);
      return Response.status(200).entity(json).build();
    }

  @GET
  @Path(value = "/project_users/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectUserByUserId(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId
    ) {
      User user = userService.GetUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<ProjectUser> projectUsers = projectUserService.findByUserId(userId);

      String json = new Gson().toJson(projectUsers);
      return Response.status(200).entity(json).build();
    }

  @POST
  @Path(value = "/project_users")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response UpdateProjectUserByUserId(
    @HeaderParam("Authorization") String authorization,
    ProjectUser projectUser
    ) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      try {
        projectUser = projectUserService.save(projectUser);
      } catch (Exception e) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", e.getMessage());
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      String json = new Gson().toJson(projectUser);
      return Response.status(200).entity(json).build();
    }

  @PATCH
  @Path(value = "/project_users/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response UpdateProjectUserByUserId(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId,
    @FormParam("project_ids") String projectIds
    ) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<ProjectUser> projectUsers = new ArrayList<ProjectUser>();
      try {
        projectUsers = projectUserService.assignProjectUsers(userDetail, userId, projectIds);
      } catch (Exception e) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", e.getMessage());
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      String json = new Gson().toJson(projectUsers);
      return Response.status(200).entity(json).build();
    }

  @DELETE
  @Path(value = "/project_users/{project_user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response DeleteProjectUserByProjectUserId(
    @HeaderParam("Authorization") String authorization,
    @PathParam("project_user_id") Integer projectUserId
    ) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ProjectUser projectUser = projectUserService.findById(projectUserId);
      projectUserService.delete(projectUser);

      return Response.ok().build();
    }
  
  @GET
  @Path(value = "/project_verificators/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectVerificatorByUserId(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId
    ) {
      User user = userService.GetUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserId(userId);

      String json = new Gson().toJson(projectVerificators);
      return Response.status(200).entity(json).build();
    }

  @PATCH
  @Path(value = "/project_verificators/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response SaveProjectVerificatorByUserId(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId,
    @FormParam("project_ids") String projectIds
    ) {
      UserDetail userDetail = userService.GetCompleteUserFromAuthorization(authorization);
      if (userDetail == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      List<ProjectVerificator> projectVerificators = new ArrayList<ProjectVerificator>();
      try {
      projectVerificators = projectVerificatorService.assignProjectVerificators(userDetail, userId, projectIds);
      } catch (Exception e) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", e.getMessage());
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      String json = new Gson().toJson(projectVerificators);
      return Response.status(200).entity(json).build();
    }
  
  @GET
  @Path(value = "/groups")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetGroups(
    @HeaderParam("Authorization") String authorization
    ) {
      User user = userService.GetUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();

      List<Group> groups = processEngine.getIdentityService().createGroupQuery().list();
      String json = new Gson().toJson(groups);
      return Response.status(200).entity(json).build();
    }
  
  @GET
  @Path(value = "/groups/client")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetGroupsClientOnly(
    @HeaderParam("Authorization") String authorization
    ) {
      User user = userService.GetUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();

      String[] ids = new String[]{"owner", "superuser", "viewer", "user"};
      List<Group> groups = processEngine.getIdentityService().createGroupQuery().groupIdIn(ids).list();
      String json = new Gson().toJson(groups);
      return Response.status(200).entity(json).build();
    }
  
  @GET
  @Path(value = "/tenants")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetTenants(
    @HeaderParam("Authorization") String authorization
    ) {
      User user = userService.GetUserFromAuthorization(authorization);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "login expired, please logout and relogin");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();

      List<Tenant> tenants = processEngine.getIdentityService().createTenantQuery().list();
      String json = new Gson().toJson(tenants);
      return Response.status(200).entity(json).build();
    }

  @GET
  @Path(value = "/tenants/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetTenantById(
    @PathParam("id") String tenantId
    ) {
      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();

      Tenant tenant = processEngine.getIdentityService().createTenantQuery().tenantId(tenantId).singleResult();

      if (tenant == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Invitation not valid");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      String json = new Gson().toJson(tenant);
      return Response.status(200).entity(json).build();
    }
  
  @POST
  @Path(value = "/reset_password")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response ResetPasswordInit(
    @FormParam("email") String email) {
      User user = userService.FindByEmail(email);;
      if (user == null) {
        return Response.status(200).build();
      }
      PasswordToken passwordToken = passwordTokenService.GenerateTokenByUserId(user.getId());
      com.sendgrid.Response response = mailerService.SendResetPasswordEmail(user, passwordToken);

      String json = new Gson().toJson(response);
      return Response.status(200).entity(json).build();
    }
  
  @GET
  @Path(value = "/reset_password/{token}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CheckResetPassword(
    @HeaderParam("Authorization") String authorization,
    @PathParam("token") String token
    ) {
      String result = passwordTokenService.ValidatePasswordResetToken(token);
      if(result != null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", result);
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      } else {
        return Response.status(200).build();
      }
    }

  @PATCH
  @Path(value = "/reset_password")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response ResetPasswordUpdate(
    @FormParam("password") String password,
    @FormParam("token") String token) {
      String result = passwordTokenService.ValidatePasswordResetToken(token);
      if (result != null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", result);
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      UserDetail user = userService.FindByToken(token);
      if (user == null) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "User not found");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }
      
      ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
      IdentityService identityService = processEngine.getIdentityService();

      if (!PasswordValidator.isValid(password)) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Password must be combination of Uppercase, lowercase, special character and minimum 8 digit");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }

      org.camunda.bpm.engine.identity.User updatedUser = identityService.createUserQuery().userId(user.getUsername()).singleResult();
      updatedUser.setPassword(password);
      identityService.saveUser(updatedUser);

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
    }
}
