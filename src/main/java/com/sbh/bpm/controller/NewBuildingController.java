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

  private SbhTask sbhTask;
  
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
    @FormDataParam("postal_code") String postalCode
    ) {      
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
    variables.put("faximile", faximile);
    variables.put("postal_code", postalCode);

    ProcessInstance instance = runtimeService.startProcessInstanceByKey("new-building-process", variables);
    String activityInstanceId = runtimeService.getActivityInstance(instance.getId()).getId();

    if (file != null) {
      GoogleCloudStorage googleCloudStorage;
      try {
        googleCloudStorage = new GoogleCloudStorage();
      } catch (IOException e) {
        e.printStackTrace();
        return Response.status(400, e.getMessage()).build();
      }
  
      String ext = FilenameUtils.getExtension(fileFdcd.getFileName());
  
      String fileName = "invoice__" + activityInstanceId + "." + ext;
  
      googleCloudStorage.SaveObject(fileName, file);
      runtimeService.setVariable(instance.getId(), "proof_of_payment", fileName);
    }
    
    JSONObject obj = new JSONObject();
    try {
      obj.put("process_definition_id", instance.getProcessDefinitionId());
      obj.put("case_instance_id", instance.getCaseInstanceId());
      obj.put("business_key", instance.getBusinessKey());
      obj.put("activity_instance_id", activityInstanceId);
      // obj.put("blob_name", blobId.getName());
      // obj.put("activityId", activityId);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    String json = new Gson().toJson(obj);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks")
  @Produces(MediaType.APPLICATION_JSON)
  public Response Variable(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().active().list();

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
    
    Map<String, Object> variableMap = taskService.getVariables(taskId);

    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(400, e.getMessage()).build();
    }

    // Get it by blob name
    String filename = String.valueOf(variableMap.get("proof_of_payment"));
    Blob blob = googleCloudStorage.GetBlobByName(filename);

    if (blob != null) {
      googleCloudStorage.SetGcsSignUrl(blob);
      String publicUrl = googleCloudStorage.GetSignedUrl();
      variableMap.put("proof_of_payment_url", publicUrl);
    }

    String provinceId = String.valueOf(variableMap.get("province"));
    if (provinceId != "null") {
      Province province = provinceService.findyById(provinceId);
      variableMap.put("province_name", province.getName());
    }

    String cityId = String.valueOf(variableMap.get("city"));
    if (cityId != "null") {
      City city = cityService.findyById(cityId);
      variableMap.put("city_name", city.getName());
    }

    return Response.ok(variableMap).build();
  }

  @GET
  @Path(value = "/diagram/{processDefinitionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDiagram(@PathParam("processDefinitionId") String processDefinitionId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();

    ProcessDefinition definition = processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);
    InputStream processDiagram = processEngine.getRepositoryService().getProcessDiagram(processDefinitionId); 
    String fileName = definition.getDiagramResourceName();

    return Response.ok(fileName).build();
  }
}