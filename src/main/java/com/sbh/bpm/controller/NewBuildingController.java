package com.sbh.bpm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.gson.Gson;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.model.SbhTask;
import com.sbh.bpm.service.GoogleCloudStorage;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IProvinceService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
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

    try {
      uploadToGcs(runtimeService, processInstance.getId(), activityInstanceId, file, fileFdcd, "proof_of_payment");
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(400, e.getMessage()).build();
    }
    
    TaskService taskService = processEngine.getTaskService();
    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().singleResult();
    task.setTenantId(tenant);
    taskService.claim(task.getId(), username);
    taskService.setAssignee(task.getId(), username);
    taskService.complete(task.getId());

    task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).orderByTaskCreateTime().desc().singleResult();
    taskService.claim(task.getId(), "admin");

    Map<String, String> map = new HashMap<String, String>();
    map.put("process_definition_id", processInstance.getProcessDefinitionId());
    map.put("case_instance_id", processInstance.getCaseInstanceId());
    map.put("business_key", processInstance.getBusinessKey());
    map.put("activity_instance_id", activityInstanceId);

    String json = new Gson().toJson(map);
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
    variableMap.put("owner", task.getOwner());
    variableMap.put("tenant_id", task.getTenantId());

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

  @POST
  @Path(value = "/accept-register-project")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response AcceptRegisterProject(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    // Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    taskService.setVariable(taskId, "building_approved", true);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/reject-register-project")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response RejectRegisterProject(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("rejected_reason") String rejectedReason
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String processInstanceId = task.getProcessInstanceId();

    taskService.setVariable(taskId, "building_approved", false);
    taskService.setVariable(taskId, "rejected_reason", rejectedReason);
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

  @POST
  @Path(value = "/upload-eligibility-document")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response UploadEligibility(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("building_plan") InputStream buildingPlan, 
    @FormDataParam("building_plan") FormDataContentDisposition buildingPlanFdcd,
    @FormDataParam("rt_rw") InputStream rtRw, 
    @FormDataParam("rt_rw") FormDataContentDisposition rtRwFdcd,
    @FormDataParam("upl_ukl") InputStream uplUkl, 
    @FormDataParam("upl_ukl") FormDataContentDisposition uplUklFdcd,
    @FormDataParam("earthquake_resistance") InputStream earthquakeResistance, 
    @FormDataParam("earthquake_resistance") FormDataContentDisposition earthquakeResistanceFdcd,
    @FormDataParam("disability_friendly") InputStream disabilityFriendly, 
    @FormDataParam("disability_friendly") FormDataContentDisposition disabilityFriendlyFdcd,
    @FormDataParam("safety_and_fire_requirement") InputStream safetyAndFireRequirement, 
    @FormDataParam("safety_and_fire_requirement") FormDataContentDisposition safetyAndFireRequirementFdcd,
    @FormDataParam("study_case_readiness") InputStream studyCaseReadiness, 
    @FormDataParam("study_case_readiness") FormDataContentDisposition studyCaseReadinessFdcd,
    @FormDataParam("task_id") String taskId
  ) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    TaskService taskService = processEngine.getTaskService();
    
    String username = "indofood1";

    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Pair<String, BlobId>>> listOfCallable = Arrays.asList(
                () -> uploadToGcs(runtimeService, processInstanceId, activityInstanceId, buildingPlan, buildingPlanFdcd, "building_plan"),
                () -> uploadToGcs(runtimeService, processInstanceId, activityInstanceId, rtRw, rtRwFdcd, "rt_rw"),
                () -> uploadToGcs(runtimeService, processInstanceId, activityInstanceId, uplUkl, uplUklFdcd, "upl_ukl"),
                () -> uploadToGcs(runtimeService, processInstanceId, activityInstanceId, earthquakeResistance, earthquakeResistanceFdcd, "earthquake_resistance"),
                () -> uploadToGcs(runtimeService, processInstanceId, activityInstanceId, disabilityFriendly, disabilityFriendlyFdcd, "disability_friendly"),
                () -> uploadToGcs(runtimeService, processInstanceId, activityInstanceId, safetyAndFireRequirement, safetyAndFireRequirementFdcd,  "safety_and_fire_requirement"),
                () -> uploadToGcs(runtimeService, processInstanceId, activityInstanceId, studyCaseReadiness, studyCaseReadinessFdcd, "study_case_readiness")
                );
    try {
      List<Future<Pair<String, BlobId>>> futures = executor.invokeAll(listOfCallable);

      Map<String, BlobId> result = new HashMap<String, BlobId>();
      futures.stream().forEach(f -> {
          try {
            Pair<String, BlobId> res = f.get();
            if (res != null) {
              result.put(res.getKey(), res.getValue());
            }
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
      });

    } catch (InterruptedException e) {// thread was interrupted
        e.printStackTrace();
      return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setAssignee(task.getId(), username);
    taskService.claim(task.getId(), username);
    taskService.complete(task.getId());

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    taskService.setAssignee(task.getId(), "admin");
    taskService.claim(task.getId(), "admin");

    return Response.ok().build();
  }

  @GET
  @Path(value = "/archived_files/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ArchivedFile(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
    } catch (NullValueException e) {
      return Response.status(400, "task id not found").build();
    }

    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(400, e.getMessage()).build();
    }

    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Blob>> listOfCallable = Arrays.asList(
                () -> getBlob(googleCloudStorage, variableMap, "proof_of_payment"),
                () -> getBlob(googleCloudStorage, variableMap, "building_plan"),
                () -> getBlob(googleCloudStorage, variableMap, "rt_rw"),
                () -> getBlob(googleCloudStorage, variableMap, "upl_ukl"),
                () -> getBlob(googleCloudStorage, variableMap, "earthquake_resistance"),
                () -> getBlob(googleCloudStorage, variableMap, "disability_friendly"),
                () -> getBlob(googleCloudStorage, variableMap, "safety_and_fire_requirement"),
                () -> getBlob(googleCloudStorage, variableMap, "study_case_readiness")
                );

    FileOutputStream fos;
    ZipOutputStream zipOut;
    try {
      fos = new FileOutputStream(taskId + ".zip");
      zipOut = new ZipOutputStream(fos);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return Response.status(400, e.getMessage()).build();
    }
    try {
      List<Future<Blob>> futures = executor.invokeAll(listOfCallable);

      futures.stream().forEach(f -> {
          try {
            Blob blob = f.get();
            if (blob != null) {
              ZipEntry zipEntry = new ZipEntry(blob.getName());
              zipOut.putNextEntry(zipEntry);
              byte[] byteArray = blob.getContent();
              zipOut.write(byteArray);
            }
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
      });

    } catch (InterruptedException e) {// thread was interrupted
        e.printStackTrace();
      return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    try {
      zipOut.close();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
      return Response.status(400, e.getMessage()).build();
    }
  

    File zipFile = new File(taskId + ".zip");
    StreamingOutput stream = new StreamingOutput() {
        @Override
        public void write(OutputStream output) throws IOException {
            try {
                output.write(IOUtils.toByteArray(new FileInputStream(zipFile)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "inline; filename=\"" + zipFile.getName() + "\"") 
        .build();
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

  @GET
  @Path(value = "/url_file/{task_id}/{file_name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetUrlFile(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId,
    @PathParam("file_name") String fileName
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
    } catch (NullValueException e) {
      return Response.status(400, "task id not found").build();
    }

    Pair<String, String> result;
    try {
      result = getUrlGcs(variableMap, fileName);
    } catch (IOException e) {
      result = null;
      return Response.status(404).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result.getValue());
    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  private Pair<String, BlobId> uploadToGcs(
    RuntimeService runtimeService,
    String processInstanceId,
    String activityInstanceId,
    InputStream file, 
    FormDataContentDisposition fileFdcd, 
    String alias
  ) throws IOException {
    if (fileFdcd.getFileName() != null) {
      String ext = FilenameUtils.getExtension(fileFdcd.getFileName());
      String fileName = activityInstanceId + "__" + alias + "." + ext;

      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      BlobId blobId = googleCloudStorage.SaveObject(fileName, file);
      runtimeService.setVariable(processInstanceId, alias, fileName);

      Pair<String, BlobId> variables = new ImmutablePair<>(alias, blobId);
      return variables;
    } else {
      return null;
    }
  }

  private Pair<String, String> getUrlGcs(Map<String, Object> variableMap, String filename) throws IOException {
    // Get it by blob name
    if (variableMap.get(filename) != null) {
      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      Blob blob = getBlob(googleCloudStorage, variableMap, filename);
  
      if (blob != null) {
        googleCloudStorage.SetGcsSignUrl(blob);
        String publicUrl = googleCloudStorage.GetSignedUrl();
        Pair<String, String> variables = new ImmutablePair<>(filename, publicUrl);
        return variables;
      }
    }

    return null;
  }

  private Blob getBlob(GoogleCloudStorage googleCloudStorage, Map<String, Object> variableMap, String filename) {
    String path = String.valueOf(variableMap.get(filename));
    return googleCloudStorage.GetBlobByName(path);
  } 
}