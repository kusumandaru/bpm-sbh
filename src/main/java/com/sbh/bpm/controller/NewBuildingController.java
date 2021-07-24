package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.google.cloud.storage.Blob;
import com.google.gson.Gson;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.model.SbhTask;
import com.sbh.bpm.service.GoogleCloudStorage;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IProvinceService;

import org.apache.commons.io.FilenameUtils;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class NewBuildingController {
  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

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
    @FormDataParam("faximile") String faximile,
    @FormDataParam("postal_code") String postalCode,
    @FormDataParam("gross_floor_area") Integer grossFloorArea
    ) {      
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    String username = "indofood1";
    String tenant = "indofood";

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
    variables.put("faximile", faximile);
    variables.put("postal_code", postalCode);
    variables.put("gross_floor_area", grossFloorArea);
    variables.put("assignee", username);
    variables.put("tenant", tenant);

    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("new-building-process", variables);
    ActivityInstance activityInstance = runtimeService.getActivityInstance(processInstance.getId());
    String activityInstanceId = activityInstance.getId();

    if (fileFdcd.getFileName() != null) {
      String ext = FilenameUtils.getExtension(fileFdcd.getFileName());
      String fileName = "invoice__" + activityInstanceId + "." + ext;

      GoogleCloudStorage googleCloudStorage;
      try {
        googleCloudStorage = new GoogleCloudStorage();
      } catch (IOException e) {
        e.printStackTrace();
        return Response.status(400, e.getMessage()).build();
      }
      googleCloudStorage.SaveObject(fileName, file);
      runtimeService.setVariable(processInstance.getId(), "proof_of_payment", fileName);
    }
    
    TaskService taskService = processEngine.getTaskService();
    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().singleResult();
    task.setTenantId(tenant);
    taskService.claim(task.getId(), username);
    taskService.setAssignee(task.getId(), username);
    taskService.complete(task.getId());

    task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().singleResult();
    taskService.claim(task.getId(), "admin");

    JSONObject obj = new JSONObject();
    try {
      obj.put("process_definition_id", processInstance.getProcessDefinitionId());
      obj.put("case_instance_id", processInstance.getCaseInstanceId());
      obj.put("business_key", processInstance.getBusinessKey());
      obj.put("activity_instance_id", activityInstanceId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    String json = new Gson().toJson(obj);
    return Response.ok(json).build();
  }

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
    
    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    Map<String, Object> variableMap = taskService.getVariables(taskId);
    variableMap.put("task_id", task.getId());
    variableMap.put("created_at", task.getCreateTime());
    variableMap.put("due_date", task.getDueDate());
    variableMap.put("owner", task.getOwner());
    variableMap.put("tenant_id", task.getTenantId());

    // Get it by blob name
    if (variableMap.get("proof_of_payment") != null) {
      GoogleCloudStorage googleCloudStorage;
      try {
        googleCloudStorage = new GoogleCloudStorage();
      } catch (IOException e) {
        e.printStackTrace();
        return Response.status(400, e.getMessage()).build();
      }
      String filename = String.valueOf(variableMap.get("proof_of_payment"));
      Blob blob = googleCloudStorage.GetBlobByName(filename);
  
      if (blob != null) {
        googleCloudStorage.SetGcsSignUrl(blob);
        String publicUrl = googleCloudStorage.GetSignedUrl();
        variableMap.put("proof_of_payment_url", publicUrl);
      }
    }

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

    return Response.ok(variableMap).build();
  }

  @GET
  @Path(value = "/accept-register-project/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AcceptRegisterProject(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    // Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    taskService.setVariable(taskId, "building_approved", true);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @GET
  @Path(value = "/reject-register-project/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response RejectRegisterProject(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String processInstanceId = task.getProcessInstanceId();

    taskService.setVariable(taskId, "building_approved", false);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    String assignee = taskService.getVariable(task.getId(), "assignee").toString();
    String tenant = taskService.getVariable(task.getId(), "tenant").toString();
    task.setAssignee(assignee);
    task.setTenantId(tenant);
    taskService.claim(task.getId(), assignee);

    return Response.ok().build();
  }


  @GET
  @Path(value = "/diagram/{processDefinitionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDiagram(@PathParam("processDefinitionId") String processDefinitionId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    
    ProcessDefinition definition = processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);
    String fileName = definition.getDiagramResourceName();

    return Response.ok(fileName).build();
  }
}