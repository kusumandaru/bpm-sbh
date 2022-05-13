package com.sbh.bpm.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.service.IUserService;

import org.apache.commons.io.FilenameUtils;
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

  @GET
  @Path(value = "/profile")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProfile(@HeaderParam("Authorization") String authorization
  ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "user not found");
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
      map.put("message", "user not found");
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
      map.put("message", "user not found");
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
      map.put("message", "user not found");
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
}
