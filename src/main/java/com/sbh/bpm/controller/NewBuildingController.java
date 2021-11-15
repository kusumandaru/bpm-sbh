package com.sbh.bpm.controller;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.storage.BlobId;
import com.google.gson.Gson;
import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.service.IBuildingTypeService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IProvinceService;
import com.sbh.bpm.service.ITransactionCreationService;
import com.sbh.bpm.service.TransactionCreationService.TransactionCreationResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class NewBuildingController extends GcsUtil{
  private static final Logger logger = LogManager.getLogger(NewBuildingController.class);
  
  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

  @Autowired
  private IBuildingTypeService buildingTypeService;

  @Autowired
  private IMailerService mailerService;

  @Autowired
  private ITransactionCreationService transactionCreationService;

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
    taskService.setVariable(taskId, "approved", true);
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

    taskService.setVariable(taskId, "approved", false);
    taskService.setVariable(taskId, "rejected_reason", rejectedReason);
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
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, buildingPlan, buildingPlanFdcd, "building_plan"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, rtRw, rtRwFdcd, "rt_rw"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, uplUkl, uplUklFdcd, "upl_ukl"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, earthquakeResistance, earthquakeResistanceFdcd, "earthquake_resistance"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, disabilityFriendly, disabilityFriendlyFdcd, "disability_friendly"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, safetyAndFireRequirement, safetyAndFireRequirementFdcd,  "safety_and_fire_requirement"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, studyCaseReadiness, studyCaseReadinessFdcd, "study_case_readiness")
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
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setAssignee(task.getId(), username);
    taskService.claim(task.getId(), username);
    taskService.complete(task.getId());

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    taskService.setVariable(task.getId(), "approved", null);
    taskService.setAssignee(task.getId(), "admin");
    taskService.claim(task.getId(), "admin");

    return Response.ok().build();
  }

  @POST
  @Path(value = "/first_payment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response FirstPayment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("agreement_letter_document") InputStream agreementLetterDocument, 
    @FormDataParam("agreement_letter_document") FormDataContentDisposition agreementLetterDocumentFdcd,
    @FormDataParam("first_payment_document") InputStream firstPaymentDocument, 
    @FormDataParam("first_payment_document") FormDataContentDisposition firstPaymentDocumentFdcd,
    @FormDataParam("agreement_number") String agreementNumber,
    @FormDataParam("first_payment") Boolean firstPayment,
    @FormDataParam("design_recognition") Boolean designRecognition,
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
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, agreementLetterDocument, agreementLetterDocumentFdcd, "agreement_letter_document"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, firstPaymentDocument, firstPaymentDocumentFdcd, "first_payment_document")
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
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "agreement_number", agreementNumber);
    taskService.setVariable(task.getId(), "first_payment", firstPayment);
    taskService.setVariable(task.getId(), "design_recognition", designRecognition);

    taskService.setVariable(task.getId(), "approved", true);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/second_payment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response SecondPayment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("second_payment_document") InputStream secondPaymentDocument, 
    @FormDataParam("second_payment_document") FormDataContentDisposition secondPaymentDocumentFdcd,
    @FormDataParam("second_payment") Boolean secondPayment,
    @FormDataParam("design_recognition") Boolean designRecognition,
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
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, secondPaymentDocument, secondPaymentDocumentFdcd, "second_payment_document")
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
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "second_payment", secondPayment);
    taskService.setVariable(task.getId(), "design_recognition", designRecognition);

    if (designRecognition) {
      TransactionCreationResponse response = transactionCreationService.createDRTransactionForProcessInstance(processInstanceId);
    }

    taskService.setVariable(task.getId(), "approved", true);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/workshop")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response Workshop(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("attendance_document") InputStream attendanceDocument, 
    @FormDataParam("attendance_document") FormDataContentDisposition attendanceDocumentFdcd,
    @FormDataParam("workshop_report_document") InputStream workshopReportDocument, 
    @FormDataParam("workshop_report_document") FormDataContentDisposition workshopReportDocumentFdcd,
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
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, attendanceDocument, attendanceDocumentFdcd, "attendance_document"),
                () -> UploadToGcs(runtimeService, processInstanceId, activityInstanceId, workshopReportDocument, workshopReportDocumentFdcd, "workshop_report_document")
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
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "approved", true);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    return Response.ok().build();
  }

}
