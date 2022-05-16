package com.sbh.bpm.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
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
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.payload.RegisterRequest;
import com.sbh.bpm.service.IUserService;

import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.engine.IdentityService;
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
  
}
