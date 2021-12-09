package com.sbh.bpm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.model.SbhTask;
import com.sbh.bpm.service.IBuildingTypeService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IPdfGeneratorUtil;
import com.sbh.bpm.service.IProvinceService;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class TaskController {
  private static final Logger logger = LogManager.getLogger(TaskController.class);

  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

  @Autowired
  private IBuildingTypeService buildingTypeService;

  @Autowired
  private IMailerService mailerService;

  @Autowired
  private IPdfGeneratorUtil pdfGeneratorUtil;

  @GET
  @Path(value = "/tasks/admin")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AdminTasks(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().taskDefinitionKeyIn("check-registration-project", "check-document-building").active().orderByTaskCreateTime().desc().list();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks/client")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ClientTasks(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().taskDefinitionKeyIn("fill-registration-project", "fill-document-building").active().orderByTaskCreateTime().desc().list();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks")
  @Produces(MediaType.APPLICATION_JSON)
  public Response Tasks(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().active().orderByTaskCreateTime().desc().list();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/variables/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response Variable(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
 
    Task task;
    Map<String, Object> variableMap;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      variableMap = taskService.getVariables(taskId);
    } catch (NullValueException e) {
      return Response.status(400, "task id not found").build();
    }

    variableMap.put("task_id", task.getId());
    variableMap.put("created_at", task.getCreateTime());
    variableMap.put("due_date", task.getDueDate());
    variableMap.put("tenant_id", task.getTenantId());
    variableMap.put("definition_key", task.getTaskDefinitionKey());

    if (variableMap.get("province") != null) {
      String provinceId = String.valueOf(variableMap.get("province"));
      Province province = provinceService.findById(Integer.parseInt(provinceId));
      variableMap.put("province_name", province.getName());
    }

    if (variableMap.get("city") != null) {
      String cityId = String.valueOf(variableMap.get("city"));
      City city = cityService.findById(Integer.parseInt(cityId));
      variableMap.put("city_name", city.getName());
    }

    try {
      BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(String.valueOf(variableMap.get("building_type"))));
      variableMap.put("building_type_name", buildingType.getNameId());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }

    return Response.ok(variableMap).build();
  }

  @POST
  @Path(value = "/read-task")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response ReadTask(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    taskService.setVariable(taskId, "read", true);
    return Response.ok().build();
  }

  @POST
  @Path(value = "/accept-task")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response AcceptTask(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    // Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    taskService.setVariable(taskId, "approved", true);
    taskService.setVariable(taskId, "read", false);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/reject-task")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response RejectTask(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("rejected_reason") String rejectedReason
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String processInstanceId = task.getProcessInstanceId();

    taskService.setVariable(taskId, "approved", false);
    taskService.setVariable(taskId, "rejected_reason", rejectedReason);
    taskService.setVariable(taskId, "read", false);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    String assignee = taskService.getVariable(task.getId(), "assignee").toString();
    String tenant = taskService.getVariable(task.getId(), "tenant").toString();
    task.setAssignee(assignee);
    task.setTenantId(tenant);
    taskService.claim(task.getId(), assignee);

    mailerService.SendRejectionEmail(rejectedReason);

    return Response.ok().build();
  }

}
