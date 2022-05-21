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

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
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
import com.sbh.bpm.model.ProjectUser;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.payload.RegisterClientRequest;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.service.IUserService;
import com.sbh.bpm.service.ProjectUserService;

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
  private IdentityService identityService;

  @Autowired
  private ProjectUserService projectUserService;

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
      return Response.status(404).build();
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

      ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin"));
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroup().getId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
        String json = new Gson().toJson(map);
        return Response.status(400).entity(json).build();
      }
      
      List<UserDetail> users = userService.findAllDetail();

      String json = new Gson().toJson(users);
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
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroup().getId()))) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Only administrator permitted");
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
      if (!adminRoles.stream().anyMatch(role -> role.equals(userDetail.getGroup().getId()))) {
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

      user.setActive(u.getActive());
      user.setEmail(u.getEmail());
      user.setFirstName(u.getFirstName());
      user.setLastName(u.getLastName());
      user.setTenantOwner(u.getTenantOwner());
      user = userService.Save(user);

      com.sbh.bpm.model.Group groupUser = userService.GroupFromUser(user);
      if (groupUser != null && !groupUser.getId().equals(u.getGroupId())) {
        identityService.deleteMembership(user.getId(), groupUser.getId());
        identityService.createMembership(user.getId(), u.getGroupId());
      }

      Tenant tenantUser = userService.TenantFromUser(user);
      if (tenantUser != null && !tenantUser.getId().equals(u.getTenantId())) {
        identityService.deleteTenantUserMembership(tenantUser.getId(), user.getId());
        identityService.createTenantUserMembership(u.getTenantId(), user.getId());
      }

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
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

      UserEntity userEntity = new UserEntity();
      userEntity.setEmail(registerRequest.getEmail());
      userEntity.setPassword(registerRequest.getPassword());
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
    User u) {
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

      user.setActive(u.getActive());
      user.setEmail(u.getEmail());
      user.setFirstName(u.getFirstName());
      user.setLastName(u.getLastName());
      user.setTenantOwner(u.getTenantOwner());
      user = userService.Save(user);

      String json = new Gson().toJson(user);
      return Response.status(200).entity(json).build();
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

      User user = userService.findById(userId);
      Tenant tenant = userService.TenantFromUser(user);
      if (user == null || !tenant.getId().equals(userDetail.getTenant().getId())) {
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

  @PATCH
  @Path(value = "/project_users/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response SaveProjectUserByUserId(
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
}
