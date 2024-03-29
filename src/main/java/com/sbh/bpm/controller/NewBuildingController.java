package com.sbh.bpm.controller;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.sbh.bpm.model.ProjectAttachment;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.service.IProjectAttachmentService;
import com.sbh.bpm.service.ITransactionCreationService;
import com.sbh.bpm.service.IUserService;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class NewBuildingController extends GcsUtil{
  private static final Logger logger = LoggerFactory.getLogger(NewBuildingController.class);

  @Autowired
  private ITransactionCreationService transactionCreationService;

  @Autowired
  private IProjectAttachmentService projectAttachmentService;

  @Autowired
  private IUserService userService;

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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
      () -> SaveWithVersion(processInstanceId, activityInstanceId, buildingPlan, buildingPlanFdcd, "building_plan", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, rtRw, rtRwFdcd, "rt_rw", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, uplUkl, uplUklFdcd, "upl_ukl", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, earthquakeResistance, earthquakeResistanceFdcd, "earthquake_resistance", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, disabilityFriendly, disabilityFriendlyFdcd, "disability_friendly", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, safetyAndFireRequirement, safetyAndFireRequirementFdcd,  "safety_and_fire_requirement", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, studyCaseReadiness, studyCaseReadinessFdcd, "study_case_readiness", username, role)
                );
    try {
      executor.invokeAll(listOfCallable);
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(task.getId());

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/agreement")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response Agreement(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("agreement_letter_document") InputStream agreementLetterDocument, 
    @FormDataParam("agreement_letter_document") FormDataContentDisposition agreementLetterDocumentFdcd,
    @FormDataParam("agreement_number") String agreementNumber,
    @FormDataParam("design_recognition") Boolean designRecognition,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
      () -> SaveWithVersion(processInstanceId, activityInstanceId, agreementLetterDocument, agreementLetterDocumentFdcd, "agreement_letter_document", username, role)
    );
    try {
      executor.invokeAll(listOfCallable);
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "agreement_number", agreementNumber);
    taskService.setVariable(task.getId(), "design_recognition", designRecognition);
    taskService.setVariable(task.getId(), "read", false);

    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/first_payment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response FirstPayment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("first_payment_document") InputStream firstPaymentDocument, 
    @FormDataParam("first_payment_document") FormDataContentDisposition firstPaymentDocumentFdcd,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
      () -> SaveWithVersion(processInstanceId, activityInstanceId, firstPaymentDocument, firstPaymentDocumentFdcd, "first_payment_document", username, role)
    );
    try {
      executor.invokeAll(listOfCallable);
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "first_payment_paid", false);
    taskService.setVariable(task.getId(), "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
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
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
                () -> SaveWithVersion(processInstanceId, activityInstanceId, secondPaymentDocument, secondPaymentDocumentFdcd, "second_payment_document", username, role)
                );
    try {
      executor.invokeAll(listOfCallable);

    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "read", false);
    taskService.setVariable(task.getId(), "second_payment_paid", false);
    taskService.setVariable(task.getId(), "approved", null);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/second_payment_approval")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response SecondPaymentApproval(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("dr_scoring_form") InputStream drScoringForm, 
    @FormDataParam("dr_scoring_form") FormDataContentDisposition drScoringFormFdcd,
    @FormDataParam("fa_scoring_form") InputStream faScoringForm, 
    @FormDataParam("fa_scoring_form") FormDataContentDisposition faScoringFormFdcd,
    @FormDataParam("approved") Boolean approved,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
      () -> SaveWithVersion(processInstanceId, activityInstanceId, drScoringForm, drScoringFormFdcd, "dr_scoring_form", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, faScoringForm, faScoringFormFdcd, "fa_scoring_form", username, role)
    );
    try {
      executor.invokeAll(listOfCallable);
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    boolean designRecognition = (Boolean) taskService.getVariable(taskId, "design_recognition");
    if (designRecognition && approved) {
      transactionCreationService.createDRTransactionForProcessInstance(processInstanceId);
    } else {
      transactionCreationService.createFATransactionForProcessInstance(processInstanceId); 
    }

    taskService.setVariable(taskId, "second_payment_paid", approved);
    taskService.setVariable(taskId, "approved", approved);
    taskService.setVariable(taskId, "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(taskId, username);
      taskService.setAssignee(taskId, username);
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/third_payment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response ThirdPayment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("third_payment_document") InputStream thirdPaymentDocument, 
    @FormDataParam("third_payment_document") FormDataContentDisposition thirdPaymentDocumentFdcd,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
      () -> SaveWithVersion(processInstanceId, activityInstanceId, thirdPaymentDocument, thirdPaymentDocumentFdcd, "third_payment_document", username, role)
    );
    try {
      executor.invokeAll(listOfCallable);
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "read", false);
    taskService.setVariable(task.getId(), "third_payment_paid", false);
    taskService.setVariable(task.getId(), "approved", null);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/workshop")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response Workshop(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("workshop_attendance_document") InputStream workshopAttendanceDocument, 
    @FormDataParam("workshop_attendance_document") FormDataContentDisposition workshopAttendanceDocumentFdcd,
    @FormDataParam("workshop_report_document") InputStream workshopReportDocument, 
    @FormDataParam("workshop_report_document") FormDataContentDisposition workshopReportDocumentFdcd,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
      () -> SaveWithVersion(processInstanceId, activityInstanceId, workshopAttendanceDocument, workshopAttendanceDocumentFdcd, "workshop_attendance_document", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, workshopReportDocument, workshopReportDocumentFdcd, "workshop_report_document", username, role)
    );
    try {
      executor.invokeAll(listOfCallable);
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    taskService.setVariable(task.getId(), "read", false);
    taskService.setVariable(task.getId(), "approved", true);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/sign_post")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response SignPost(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("sign_post") InputStream signPostDocument, 
    @FormDataParam("sign_post") FormDataContentDisposition signPostDocumentFdcd,
    @FormDataParam("approval_building_release") InputStream approvalBuildingReleaseDocument, 
    @FormDataParam("approval_building_release") FormDataContentDisposition approvalBuildingReleaseDocumentDocumentFdcd,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    //limit the number of actual threads
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<ProjectAttachment>> listOfCallable = Arrays.asList(
      () -> SaveWithVersion(processInstanceId, activityInstanceId, signPostDocument, signPostDocumentFdcd, "sign_post", username, role),
      () -> SaveWithVersion(processInstanceId, activityInstanceId, approvalBuildingReleaseDocument, approvalBuildingReleaseDocumentDocumentFdcd, "approval_building_release", username, role)
    );
    try {
      executor.invokeAll(listOfCallable);
    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    return Response.ok().build();
  }


  @POST
  @Path(value = "/dr_evaluation_assessment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DREvaluationAssessment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    String fileType = "dr_evaluation_assessment";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/dr_revision_submission")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DRRevisionSubmission(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    String fileType = "dr_revision_submission";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/fa_evaluation_assessment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response FAEvaluationAssessment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    String fileType = "fa_evaluation_assessment";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/fa_revision_submission")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response FARevisionSubmission(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId
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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    String fileType = "fa_revision_submission";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/on_site_verification_submission")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response OnSiteVerificationSubmission(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("approval_status") Boolean approvalStatus,
    @FormDataParam("on_site_note") String onSiteNote

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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    String fileType = "on_site_verification_submission";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    taskService.setVariable(task.getId(), "on_site_note", onSiteNote);
    taskService.setVariable(task.getId(), "rejected_reason", onSiteNote);
    taskService.setVariable(task.getId(), "read", false);
    taskService.setVariable(task.getId(), "approved", approvalStatus);
    taskService.setVariable(task.getId(), "on_site_approved", approvalStatus);

    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/on_site_revision_submission")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response OnSiteRevisionSubmission(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("on_site_client_revision_note") String onSiteClientRevisionNote

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
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();

    String fileType = "on_site_revision_submission";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    taskService.setVariable(taskId, "on_site_client_revision_note", onSiteClientRevisionNote);
    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(task.getId(), user.getId());
      taskService.setAssignee(task.getId(), user.getId());
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @GET
  @Path(value = "/project_attachments/{task_id}/file_type/{file_type}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectAttachmentsByFileType(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId,
    @PathParam("file_type") String fileType
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();

    List<ProjectAttachment> attachments = projectAttachmentService.findByProcessInstanceIDAndFileType(processInstanceId, fileType);

    String json = new Gson().toJson(attachments);
    return Response.status(200).entity(json).build();
  }
}
