package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
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
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.service.IProjectUserService;
import com.sbh.bpm.service.IUserService;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class ProjectController extends GcsUtil{
  private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

  @Autowired
  private IUserService userService;

  @Autowired
  private IProjectUserService projectUserService;


  @POST
  @Path(value = "/create-project")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response CreateProject(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("file") InputStream file, 
    @FormDataParam("file") FormDataContentDisposition fileFdcd,
    @FormDataParam("certification_type") String certificationType,
    @FormDataParam("building_type") String buildingType,
    @FormDataParam("building_name") String buildingName,
    @FormDataParam("owner") String owner,
    @FormDataParam("person_in_charge") String personInCharge,
    @FormDataParam("building_address") String buildingAddress,
    @FormDataParam("city") String city,
    @FormDataParam("province") String province,
    @FormDataParam("telephone") String telephone,
    @FormDataParam("handphone") String handphone,
    @FormDataParam("email") String email,
    @FormDataParam("faximile") String faximile,
    @FormDataParam("postal_code") String postalCode,
    @FormDataParam("gross_floor_area") Integer grossFloorArea,
    @FormDataParam("design_recognition") Boolean designRecognition
    ) {   
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    String username = user.getUsername();
    String role = user.getGroup().getId();

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    Map<String, Object> variables = new HashMap<String,Object>();
    variables.put("certification_type", certificationType);
    variables.put("building_type", buildingType);
    variables.put("building_name", buildingName);
    variables.put("owner", owner);
    variables.put("person_in_charge", personInCharge);
    variables.put("building_address", buildingAddress);
    variables.put("province", province);
    variables.put("city", city);
    variables.put("telephone", telephone);
    variables.put("handphone", handphone);
    variables.put("email", email);
    variables.put("faximile", faximile);
    variables.put("postal_code", postalCode);
    variables.put("gross_floor_area", grossFloorArea);
    variables.put("design_recognition", designRecognition);
    variables.put("assignee", username);
    variables.put("tenant", user.getTenant().getId());
    variables.put("approved", null);

    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("new-building-process", variables);
    ActivityInstance activityInstance = runtimeService.getActivityInstance(processInstance.getId());
    String activityInstanceId = activityInstance.getId();

    try {
      SaveWithVersion(processInstance.getId(), activityInstanceId, file, fileFdcd, "proof_of_payment", username, role);

    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
    
    TaskService taskService = processEngine.getTaskService();
    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().singleResult();
    taskService.claim(task.getId(), username);
    taskService.setAssignee(task.getId(), username);
    taskService.complete(task.getId());

    task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().singleResult();
    taskService.setVariable(task.getId(), "tenant", user.getTenant().getId());
    taskService.addCandidateGroup(task.getId(), "admin");

    ProjectUser projectUser = new ProjectUser(user.getUsername(), user.getTenant().getId(), processInstance.getId(), null, true);
    projectUserService.save(projectUser);

    Map<String, String> map = new HashMap<String, String>();
    map.put("process_definition_id", processInstance.getProcessDefinitionId());
    map.put("case_instance_id", processInstance.getCaseInstanceId());
    map.put("business_key", processInstance.getBusinessKey());
    map.put("activity_instance_id", activityInstanceId);

    String json = new Gson().toJson(map);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/edit-project/{taskId}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response EditProject(
    @PathParam("taskId") String taskId,
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("file") InputStream file, 
    @FormDataParam("file") FormDataContentDisposition fileFdcd,
    @FormDataParam("certification_type") String certificationType,
    @FormDataParam("building_type") String buildingType,
    @FormDataParam("building_name") String buildingName,
    @FormDataParam("owner") String owner,
    @FormDataParam("person_in_charge") String personInCharge,
    @FormDataParam("building_address") String buildingAddress,
    @FormDataParam("city") String city,
    @FormDataParam("province") String province,
    @FormDataParam("telephone") String telephone,
    @FormDataParam("handphone") String handphone,
    @FormDataParam("email") String email,
    @FormDataParam("faximile") String faximile,
    @FormDataParam("postal_code") String postalCode,
    @FormDataParam("gross_floor_area") Integer grossFloorArea,
    @FormDataParam("design_recognition") Boolean designRecognition
    ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    String username = user.getUsername();
    String role = user.getGroup().getId();

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    String processInstanceId;
    String activityInstanceId;

    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
      activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    Map<String, String> variables = new HashMap<String,String>();
    variables.put("certification_type", certificationType);
    variables.put("building_type", buildingType);
    variables.put("building_name", buildingName);
    variables.put("owner", owner);
    variables.put("person_in_charge", personInCharge);
    variables.put("building_address", buildingAddress);
    variables.put("province", province);
    variables.put("city", city);
    variables.put("telephone", telephone);
    variables.put("handphone", handphone);
    variables.put("email", email);
    variables.put("faximile", faximile);
    variables.put("postal_code", postalCode);

    for (Map.Entry<String, String> variable : variables.entrySet()) {
      if (!StringUtils.isEmpty(variable.getValue())) {
        taskService.setVariable(task.getId(), variable.getKey(), variable.getValue());
      }
    }
    if (grossFloorArea > 0) {
      taskService.setVariable(task.getId(), "gross_floor_area", grossFloorArea);
    }
    taskService.setVariable(task.getId(), "design_recognition", designRecognition);
    taskService.setVariable(task.getId(), "approved", null);

    try {
      SaveWithVersion(processInstanceId, activityInstanceId, file, fileFdcd, "proof_of_payment", username, role);
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    taskService.setVariable(task.getId(), "tenant", user.getTenant().getId());
    taskService.claim(task.getId(), username);
    taskService.setAssignee(task.getId(), username);
    taskService.complete(task.getId());

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    taskService.claim(task.getId(), "admin");

    Map<String, String> map = new HashMap<String, String>();
    map.put("process_instance_id", processInstanceId);
    map.put("activity_instance_id", activityInstanceId);

    String json = new Gson().toJson(map);
    return Response.ok(json).build();
  }
}
