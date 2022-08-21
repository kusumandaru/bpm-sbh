package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.sbh.bpm.model.Attachment;
import com.sbh.bpm.model.Comment;
import com.sbh.bpm.model.CriteriaScoring;
import com.sbh.bpm.model.DocumentFile;
import com.sbh.bpm.model.ExerciseAssessment;
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.model.MasterCriteriaBlocker;
import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.model.MasterLevel;
import com.sbh.bpm.model.ProjectAssessment;
import com.sbh.bpm.model.ProjectAttachment;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.service.IAttachmentService;
import com.sbh.bpm.service.ICommentService;
import com.sbh.bpm.service.ICriteriaScoringService;
import com.sbh.bpm.service.IDocumentFileService;
import com.sbh.bpm.service.IExerciseAssessmentService;
import com.sbh.bpm.service.IMasterCriteriaBlockerService;
import com.sbh.bpm.service.IMasterCriteriaService;
import com.sbh.bpm.service.IMasterEvaluationService;
import com.sbh.bpm.service.IMasterExerciseService;
import com.sbh.bpm.service.IMasterLevelService;
import com.sbh.bpm.service.IProjectAssessmentService;
import com.sbh.bpm.service.IProjectAttachmentService;
import com.sbh.bpm.service.ITransactionCreationService;
import com.sbh.bpm.service.ITransactionFetchService;
import com.sbh.bpm.service.IUserService;
import com.sbh.bpm.service.TransactionCreationService.TransactionCreationResponse;
import com.sbh.bpm.service.TransactionFetchService.TransactionFetchResponse;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class AssessmentController extends GcsUtil {
  // private static final Logger logger = LoggerFactory.getLogger(AssessmentController.class);
  
  @Autowired
  private ITransactionCreationService transactionCreationService;

  @Autowired
  private ITransactionFetchService transactionFetchService;
  
  @Autowired
  private IProjectAssessmentService projectAssessmentService;

  @Autowired
  private IExerciseAssessmentService exerciseAssessmentService;

  @Autowired
  private ICriteriaScoringService criteriaScoringService;

  @Autowired
  private IDocumentFileService documentFileService;

  @Autowired
  private IMasterEvaluationService masterEvaluationService;

  @Autowired
  private IMasterExerciseService masterExerciseService;

  @Autowired
  private IMasterCriteriaService masterCriteriaService;

  @Autowired
  private IMasterCriteriaBlockerService masterCriteriaBlockerService;

  @Autowired
  private ICommentService commentService;

  @Autowired
  private IAttachmentService attachmentService;

  @Autowired
  private IProjectAttachmentService projectAttachmentService;

  @Autowired
  private IMasterLevelService masterLevelService;

  @Autowired
  private IUserService userService;

  @POST
  @Path(value = "/design_recognition")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionCreation(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) { 
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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

    TransactionCreationResponse response = transactionCreationService.createDRTransactionForProcessInstance(processInstanceId);

    String json = new Gson().toJson(response);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/design_recognition/{task_id}/submission")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response PostDesignRecognitionSubmission(
    @HeaderParam("Authorization") String authorization,
    @PathParam("task_id") String taskId
  ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    String username = user.getUsername();

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
    transactionCreationService.tagSubmittedAttachmentByAssessmentType(processInstanceId, "DR");

    taskService.setVariable(taskId, "tenant", (user.getTenant().getId()));
    if (taskService.getVariable(taskId, "third_payment_paid") == null) {
      taskService.setVariable(taskId, "third_payment_paid", false);
    }
    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);
    taskService.claim(task.getId(), username);
    taskService.setAssignee(task.getId(), username);
    taskService.complete(task.getId());

    return Response.status(200).build();
  }

  @GET
  @Path(value = "/design_recognition/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionFetch(
    @HeaderParam("Authorization") String authorization,
    @PathParam("taskId") String taskId
  ) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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

    TransactionFetchResponse response = transactionFetchService.GetDRTransactionForProcessInstance(processInstanceId);

    String json = new Gson().toJson(response);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/design_recognition/{task_id}/assessment_attachments")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDesignRecognitionAttachmentAssessments(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {   
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    String fileType = "dr_assessment_attachment";

    List<ProjectAttachment> attachments = projectAttachmentService.findByProcessInstanceIDAndFileType(processInstanceId, fileType);

    String json = new Gson().toJson(attachments);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/design_recognition/{task_id}/assessment_attachment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response PostDesignRecognitionAssessmentAttachment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @PathParam("task_id") String taskId
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

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "DR");
    ProjectAssessment projectAssessment = projectAssessments.get(0);
    String fileType = "dr_assessment_attachment";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", e.getMessage());
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    String json = new Gson().toJson(projectAssessment);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/design_recognition/{task_id}/review_dr")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response DrReview(@HeaderParam("Authorization") String authorization,
                                @PathParam("task_id") String taskId,
                                @FormDataParam("approval_type") String approvalType,
                                @FormDataParam("review_reason") String reviewReason) {
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String processInstanceId = task.getProcessInstanceId();

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "DR");
    projectAssessments.forEach(p -> { 
      p.setApprovalNote(reviewReason);
      p.setApprovalStatus(approvalType);
      projectAssessmentService.save(p);
    });

    boolean approvalStatus = !approvalType.equals("rejected");
    taskService.setVariable(taskId, "dr_approved", approvalStatus);
    taskService.setVariable(taskId, "approved", approvalStatus);
    taskService.setVariable(taskId, "review_reason", reviewReason);
    if (approvalStatus == false) {
      taskService.setVariable(taskId, "rejected_reason", reviewReason);
    }
    taskService.setVariable(taskId, "read", false);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    String assignee = taskService.getVariable(task.getId(), "assignee").toString();

    task.setAssignee(assignee);
    taskService.claim(task.getId(), assignee);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/design_recognition/{task_id}/update_level")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response PostDRDesignRecognitionUpdateLevel(
    @HeaderParam("Authorization") String authorization,
    @PathParam("task_id") String taskId,
    @FormDataParam("level_id") Integer levelId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "DR");
    ProjectAssessment projectAssessment = projectAssessments.get(0);
 
    projectAssessment.setProposedLevelID(levelId);
    projectAssessment =  projectAssessmentService.save(projectAssessment);

    String json = new Gson().toJson(projectAssessment);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/design_recognition/{task_id}/assessment_attachment")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDRAssessmentAttachmentUrlFile(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "DR");
    ProjectAssessment projectAssessment = projectAssessments.get(0);
    String attachmentLink = projectAssessment.getAssessmentAttachment();

    String result;
    try {
      result = GetUrlGcs(attachmentLink);
    } catch (IOException e) {
      result = null;
      return Response.status(400).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result);
    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/design_recognition/{task_id}/eligible_submit")
  @Produces(MediaType.APPLICATION_JSON)
  public Response EligibleSubmit(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceID(processInstanceId);
    ProjectAssessment projectAssessment = projectAssessments.get(0);

    MasterLevel minimumLevel = (MasterLevel) masterLevelService.findFirstByMasterTemplateIDOrderByMinimumScoreAsc(projectAssessment.getMasterTemplateID());
    Boolean prequisiteScore = (projectAssessment.getSubmittedScore() + projectAssessment.getApprovedScore() + projectAssessment.getScoreModifier()) >= minimumLevel.getMinimumScore();
  
    List<MasterCriteria> unselectedMasterCriterias = masterCriteriaService.findByProjectAssessmentIDAndSelectedAndPrequisite(projectAssessment.getId(), false);
    List<String> unselectedMasterCriteriaCodes = unselectedMasterCriterias.stream().map(MasterCriteria::getCode).collect(Collectors.toList());
    Boolean prequisiteTask = unselectedMasterCriterias.isEmpty();

    String fileType = "dr_assessment_attachment";
    List<ProjectAttachment> projectAttachments = projectAttachmentService.findByProcessInstanceIDAndFileType(processInstanceId, fileType);
    Boolean prequisiteAttachment = !projectAttachments.isEmpty();

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("eligible", prequisiteScore && prequisiteAttachment && prequisiteTask);
    map.put("score", prequisiteScore);
    map.put("attachment", prequisiteAttachment);
    map.put("prequisite", prequisiteTask);
    map.put("prequisite_codes", unselectedMasterCriteriaCodes);

    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/design_recognition/{task_id}/eligible_approve")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DREligiblApprove(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceID(processInstanceId);
    ProjectAssessment projectAssessment = projectAssessments.get(0);
    
    List<CriteriaScoring> underReviewScorings = criteriaScoringService.findByProjectAssessmentIDAndApprovalStatusIn(projectAssessment.getId(), Arrays.asList(2));
    List<MasterCriteria> underReviewMasterCriterias = underReviewScorings.stream().map(CriteriaScoring::getCriteria).collect(Collectors.toList());
    List<String> underReviewCriteriaCodes = underReviewMasterCriterias.stream().map(MasterCriteria::getCode).collect(Collectors.toList());

    List<CriteriaScoring> rejectedReviewScorings = criteriaScoringService.findByProjectAssessmentIDAndApprovalStatusIn(projectAssessment.getId(), Arrays.asList(3));
    List<MasterCriteria> rejectedMasterCriterias = rejectedReviewScorings.stream().map(CriteriaScoring::getCriteria).collect(Collectors.toList());
    List<String> rejectedCriteriaCodes = rejectedMasterCriterias.stream().map(MasterCriteria::getCode).collect(Collectors.toList());

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("eligible", underReviewScorings.isEmpty());
    map.put("criteria_codes", underReviewCriteriaCodes);
    map.put("eligible_approved", underReviewScorings.isEmpty() && rejectedCriteriaCodes.isEmpty());
    map.put("rejected_criteria_codes", rejectedCriteriaCodes);

    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/design_recognition/{taskId}/project_assessment")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecogitionProjectAssessment(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
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
    String processInstanceID = task.getProcessInstanceId();

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, "DR");

    String json = new Gson().toJson(projectAssessments);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/design_recognition/{taskId}/evaluations")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDRMasterEvaluationByTaskID(@HeaderParam("Authorization") String authorization, @PathParam("taskId") String taskId) {      
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
    String processInstanceID = task.getProcessInstanceId();

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, "DR");
    List<Integer> masterTemplateIds = projectAssessments.stream().map(ProjectAssessment::getMasterTemplateID).collect(Collectors.toList());

    List<MasterEvaluation> evaluations = masterEvaluationService.findByMasterTemplateIDIn(masterTemplateIds);

    String json = new Gson().toJson(evaluations);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/design_recognition/{taskId}/evaluation_scores")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDREvaluationScoreByTaskID(@HeaderParam("Authorization") String authorization, @PathParam("taskId") String taskId) {      
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

    List<MasterEvaluation> evaluations = transactionFetchService.GetEvaluationScoreForProcessInstance(processInstanceId, "DR");
    
    String json = new Gson().toJson(evaluations);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/final_assessment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response FinalAssessmentCreation(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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

    TransactionCreationResponse response = transactionCreationService.createFATransactionForProcessInstance(processInstanceId);

    String json = new Gson().toJson(response);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/final_assessment/{task_id}/submission")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response PostFinalAssessmentSubmission(
    @HeaderParam("Authorization") String authorization,
    @PathParam("task_id") String taskId
  ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    String username = user.getUsername();

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

    transactionCreationService.tagSubmittedAttachmentByAssessmentType(processInstanceId, "FA");

    taskService.setVariable(taskId, "tenant", user.getTenant().getId());
    if (taskService.getVariable(taskId, "third_payment_paid") == null) {
      taskService.setVariable(taskId, "third_payment_paid", false);
    }
    taskService.setVariable(task.getId(), "approved", null);
    taskService.setVariable(task.getId(), "read", false);
    taskService.claim(task.getId(), username);
    taskService.setAssignee(task.getId(), username);
    taskService.complete(task.getId());

    return Response.status(200).build();
  }

  @GET
  @Path(value = "/final_assessment/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response FinalAssessmentFetch(
    @HeaderParam("Authorization") String authorization,
    @PathParam("taskId") String taskId
  ) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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

    TransactionFetchResponse response = transactionFetchService.GetFATransactionForProcessInstance(processInstanceId);

    String json = new Gson().toJson(response);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/final_assessment/{task_id}/assessment_attachments")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetFAAttachmentAssessments(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    String fileType = "fa_assessment_attachment";

    List<ProjectAttachment> attachments = projectAttachmentService.findByProcessInstanceIDAndFileType(processInstanceId, fileType);

    String json = new Gson().toJson(attachments);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/final_assessment/{task_id}/assessment_attachment")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response PostFinalAssessmentAssessmentAttachment(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @PathParam("task_id") String taskId
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

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "FA");
    ProjectAssessment projectAssessment = projectAssessments.get(0);
    String fileType = "fa_assessment_attachment";
 
    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
      }
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", e.getMessage());
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    String json = new Gson().toJson(projectAssessment);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/final_assessment/{task_id}/review_fa")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response FaReview(@HeaderParam("Authorization") String authorization,
                                @PathParam("task_id") String taskId,
                                @FormDataParam("approval_type") String approvalType,
                                @FormDataParam("review_reason") String reviewReason) {
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
                      
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String processInstanceId = task.getProcessInstanceId();

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "DR");
    projectAssessments.forEach(p -> { 
      p.setApprovalNote(reviewReason);
      p.setApprovalStatus(approvalType);
      projectAssessmentService.save(p);
    });

    boolean approvalStatus = !approvalType.equals("rejected");
    taskService.setVariable(taskId, "fa_approved", approvalStatus);
    taskService.setVariable(taskId, "approved", approvalStatus);
    taskService.setVariable(taskId, "review_reason", reviewReason);
    if (approvalStatus == false) {
      taskService.setVariable(taskId, "rejected_reason", reviewReason);
    }
    taskService.setVariable(taskId, "read", false);
    taskService.claim(taskId, "admin");
    taskService.complete(taskId);

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    String assignee = taskService.getVariable(task.getId(), "assignee").toString();

    task.setAssignee(assignee);
    taskService.claim(task.getId(), assignee);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/final_assessment/{task_id}/update_level")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response PostFADesignRecognitionUpdateLevel(
    @HeaderParam("Authorization") String authorization,
    @PathParam("task_id") String taskId,
    @FormDataParam("level_id") Integer levelId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "FA");
    ProjectAssessment projectAssessment = projectAssessments.get(0);
 
    projectAssessment.setProposedLevelID(levelId);
    projectAssessment =  projectAssessmentService.save(projectAssessment);

    String json = new Gson().toJson(projectAssessment);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/final_assessment/{task_id}/assessment_attachment")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetFAAssessmentAttachmentUrlFile(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "FA");
    ProjectAssessment projectAssessment = projectAssessments.get(0);
    String attachmentLink = projectAssessment.getAssessmentAttachment();

    String result;
    try {
      result = GetUrlGcs(attachmentLink);
    } catch (IOException e) {
      result = null;
      return Response.status(400).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result);
    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/final_assessment/{task_id}/eligible_submit")
  @Produces(MediaType.APPLICATION_JSON)
  public Response FAEligibleSubmit(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "FA");
    ProjectAssessment projectAssessment = projectAssessments.get(0);

    MasterLevel minimumLevel = (MasterLevel) masterLevelService.findFirstByMasterTemplateIDOrderByMinimumScoreAsc(projectAssessment.getMasterTemplateID());
    Boolean prequisiteScore = (projectAssessment.getSubmittedScore() + projectAssessment.getApprovedScore() + projectAssessment.getScoreModifier()) >= minimumLevel.getMinimumScore();
  
    List<MasterCriteria> unselectedMasterCriterias = masterCriteriaService.findByProjectAssessmentIDAndSelectedAndPrequisite(projectAssessment.getId(), false);
    List<String> unselectedMasterCriteriaCodes = unselectedMasterCriterias.stream().map(MasterCriteria::getCode).collect(Collectors.toList());
    Boolean prequisiteTask = unselectedMasterCriterias.isEmpty();

    String fileType = "fa_assessment_attachment";
    List<ProjectAttachment> projectAttachments = projectAttachmentService.findByProcessInstanceIDAndFileType(processInstanceId, fileType);
    Boolean prequisiteAttachment = !projectAttachments.isEmpty();

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("eligible", prequisiteScore && prequisiteAttachment && prequisiteTask);
    map.put("score", prequisiteScore);
    map.put("attachment", prequisiteAttachment);
    map.put("prequisite", prequisiteTask);
    map.put("prequisite_codes", unselectedMasterCriteriaCodes);

    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/final_assessment/{task_id}/eligible_approve")
  @Produces(MediaType.APPLICATION_JSON)
  public Response FAEligiblApprove(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

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
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "FA");
    ProjectAssessment projectAssessment = projectAssessments.get(0);
    
    List<CriteriaScoring> underReviewScorings = criteriaScoringService.findByProjectAssessmentIDAndApprovalStatusIn(projectAssessment.getId(), Arrays.asList(2));
    List<MasterCriteria> masterCriterias = underReviewScorings.stream().map(CriteriaScoring::getCriteria).collect(Collectors.toList());
    List<String> underReviewCriteriaCodes = masterCriterias.stream().map(MasterCriteria::getCode).collect(Collectors.toList());

    List<CriteriaScoring> rejectedReviewScorings = criteriaScoringService.findByProjectAssessmentIDAndApprovalStatusIn(projectAssessment.getId(), Arrays.asList(3));
    List<MasterCriteria> rejectedMasterCriterias = rejectedReviewScorings.stream().map(CriteriaScoring::getCriteria).collect(Collectors.toList());
    List<String> rejectedCriteriaCodes = rejectedMasterCriterias.stream().map(MasterCriteria::getCode).collect(Collectors.toList());

    Map<String, Object> map = new HashMap<String, Object>();
    map.put("eligible", underReviewScorings.isEmpty());
    map.put("criteria_codes", underReviewCriteriaCodes);
    map.put("eligible_approved", underReviewScorings.isEmpty() && rejectedCriteriaCodes.isEmpty());
    map.put("rejected_criteria_codes", rejectedCriteriaCodes);

    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/final_assessment/{taskId}/project_assessment")
  @Produces(MediaType.APPLICATION_JSON)
  public Response FinalAssessmentProjectAssessment(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
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

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, "FA");

    String json = new Gson().toJson(projectAssessments);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/final_assessments/{taskId}/evaluations")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetFAMasterEvaluationByTaskID(@HeaderParam("Authorization") String authorization, @PathParam("taskId") String taskId) {      
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
    String processInstanceID = task.getProcessInstanceId();

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, "FA");
    List<Integer> masterTemplateIds = projectAssessments.stream().map(ProjectAssessment::getMasterTemplateID).collect(Collectors.toList());

    List<MasterEvaluation> evaluations = masterEvaluationService.findByMasterTemplateIDIn(masterTemplateIds);

    String json = new Gson().toJson(evaluations);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/final_assessment/{taskId}/evaluation_scores")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetFAEvaluationScoreByTaskID(@HeaderParam("Authorization") String authorization, @PathParam("taskId") String taskId) {      
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

    List<MasterEvaluation> evaluations = transactionFetchService.GetEvaluationScoreForProcessInstance(processInstanceId, "FA");
    
    String json = new Gson().toJson(evaluations);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/project_assessment/{projectAsessmentId}/exercise_assessments")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ExerciseAssessmentByProjectAssessment(@PathParam("projectAsessmentId") Integer projectAsessmentId, @HeaderParam("Authorization") String authorization) { 
    List<ExerciseAssessment> exerciseAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAsessmentId);

    String json = new Gson().toJson(exerciseAssessments);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project_assessment/{projectAsessmentId}/criteria_scorings")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CriteriaScoringByProjectAssessment(@PathParam("projectAsessmentId") Integer projectAsessmentId, @HeaderParam("Authorization") String authorization) { 
    List<CriteriaScoring> criteriaScorings = criteriaScoringService.findByProjectAssessmentID(projectAsessmentId);

    String json = new Gson().toJson(criteriaScorings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project_assessment/{projectAsessmentId}/document_files")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentFileByProjectAssessment(@PathParam("projectAsessmentId") Integer projectAsessmentId, @HeaderParam("Authorization") String authorization) { 
    List<CriteriaScoring> criteriaScorings = criteriaScoringService.findByProjectAssessmentID(projectAsessmentId);
    List<Integer> criteriaScoringIds = criteriaScorings.stream().map(CriteriaScoring::getId).collect(Collectors.toList());

    List<DocumentFile> documentFiles = documentFileService.findByCriteriaScoringIDIn(criteriaScoringIds);
    String json = new Gson().toJson(documentFiles);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/criteria_scoring/{criteriaScoringId}/document_files")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentFileByCriteriaScoring(@PathParam("criteriaScoringId") Integer criteriaScoringId, @HeaderParam("Authorization") String authorization) { 
    List<DocumentFile> documentFiles = documentFileService.findByCriteriaScoringID(criteriaScoringId);

    String json = new Gson().toJson(documentFiles);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/criteria_scoring/{criteriaScoringId}/comments")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CommentByCriteriaScoring(@PathParam("criteriaScoringId") Integer criteriaScoringId, @HeaderParam("Authorization") String authorization) { 
    List<Comment> comments = commentService.findByCriteriaScoringID(criteriaScoringId);

    String json = new Gson().toJson(comments);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/criteria_scoring/{criteriaScoringId}/comments")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionCommentCreation(
    @HeaderParam("Authorization") String authorization,
    @PathParam("criteriaScoringId") Integer criteriaScoringId,
    Comment comment
  ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    comment.setCreatedAt(new Date());
    comment.setRole(user.getGroup().getId());
    comment.setUserID(user.getUsername());
    comment = commentService.save(comment);

    String json = new Gson().toJson(comment);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/criteria_scoring/{criteriaScoringId}/additional_notes")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionCommentCreation(
    @HeaderParam("Authorization") String authorization,
    @PathParam("criteriaScoringId") Integer criteriaScoringId,
    CriteriaScoring criteriaScoring
  ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    CriteriaScoring scoring = criteriaScoringService.findById(criteriaScoringId);

    scoring.setAdditionalNotes(criteriaScoring.getAdditionalNotes());
    scoring = criteriaScoringService.save(scoring);
    String json = new Gson().toJson(scoring);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/attachments/{attachmentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAttachmentUrlFile(@HeaderParam("Authorization") String authorization, 
    @PathParam("attachmentId") Integer attachmentId
  ) {
    Attachment attachment = attachmentService.findById(attachmentId);
    String attachmentLink = attachment.getLink();

    String result;
    try {
      result = GetUrlGcs(attachmentLink);
    } catch (IOException e) {
      result = null;
      return Response.status(400).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result);
    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/attachments")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentAttachmentCreation(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("document_id") Integer documentId

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

    DocumentFile documentFile = documentFileService.findById(documentId);
    List<Attachment> attachments = new ArrayList<Attachment>();

    try{
      List<String> filenames = new ArrayList<String>();
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        if (meta.getFileName() == null){
          continue;
        }
        String filename = meta.getFileName().replaceAll(" ", "_").toLowerCase();

        BlobId blobId = UploadToGcs(activityInstanceId, is, filename);

        boolean exist = attachmentService.existsAttachmentByFilenameAndDocumentFileID(filename, documentId);
        if (exist) {
          continue;
        }
        
        Attachment attachment = new Attachment();
        attachment.setCreatedAt(new Date());
        attachment.setRole(role);
        attachment.setUploaderID(username);
        attachment.setFilename(filename);
        attachment.setLink(blobId.getName());
        attachment.setDocumentFileID(documentId);
        attachment.setCriteriaCode(documentFile.getDocument().getCriteriaCode());

        attachments.add(attachment);
        filenames.add(attachment.getFilename());
      }
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", e.getMessage());
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    Iterable<Attachment> attachs = attachmentService.saveAll(attachments);
    
    String json = new Gson().toJson(attachs);
    return Response.status(200).entity(json).build();
  }

  @DELETE
  @Path(value = "/attachments/{attachmentId}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response AttachmentDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("attachmentId") Integer attachmentId

  ) {
    boolean status = attachmentService.deleteById(attachmentId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/criteria_scoring/{criteriaScoringId}/take_score")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response TakeScore(@HeaderParam("Authorization") String authorization,
                                @PathParam("criteriaScoringId") Integer criteriaScoringId,
                                CriteriaScoring scoring
    ) {
    CriteriaScoring criteriaScoring = criteriaScoringService.findById(criteriaScoringId);
    
    List<CriteriaScoring> scoringSelecteds = criteriaScoringService.findByProjectAssessmentIDAndApprovalStatusIn(criteriaScoring.getProjectAssessmentID(), Arrays.asList(2,4));
    List<Integer> criteriaIds = scoringSelecteds.stream().map(CriteriaScoring::getMasterCriteriaID).collect(Collectors.toList());
      
    List<MasterCriteriaBlocker> blockers = masterCriteriaBlockerService.findBymasterCriteriaIDIn(criteriaIds);
    Integer criteriaId = criteriaScoring.getCriteria().getId();
    List<MasterCriteriaBlocker> blockerFounds = blockers.stream().filter(block -> block.getBlockerID().equals(criteriaId)).collect(Collectors.toList());
    
    if (blockerFounds.size() > 0) {
      JSONObject json = new JSONObject();
      try {
        List<String> blockerLists = blockerFounds.stream().map(b -> b.getCriteria().getCode()).collect(Collectors.toList());
        String blockerMessages = String.join(",", blockerLists);
        json.put("message", "Cannot take this criteria since already taken other score " + blockerMessages);
      } catch (Exception e) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", e.getMessage());
        String jsonString = new Gson().toJson(map);;
        return Response.status(400).entity(jsonString.toString()).build();
      }
      return Response.status(400).entity(json.toString()).build();
    }

    criteriaScoring.setApprovalStatus(2);
    criteriaScoring.setSelected(true);

    if (criteriaScoring.getCriteria().getExerciseType().equals("max_score")) {
      criteriaScoring.setSubmittedScore(scoring.getSubmittedScore());
    } else {
      if (criteriaScoring.getCriteria().getScore() != null) {
        criteriaScoring.setSubmittedScore(criteriaScoring.getCriteria().getScore());
      }
    }

    criteriaScoring = criteriaScoringService.save(criteriaScoring);
    ExerciseAssessment selectedExerciseAssessment = exerciseAssessmentService.findById(criteriaScoring.getExerciseAssessmentID());
    MasterExercise selectedExercise = masterExerciseService.findById(selectedExerciseAssessment.getMasterExerciseID());

    selectedExerciseAssessment.setScoreModifier(selectedExercise.getScoreModifier());
    exerciseAssessmentService.save(selectedExerciseAssessment);

    Integer projectAssessmentId = criteriaScoring.getProjectAssessmentID();
    List<CriteriaScoring> allScorings = criteriaScoringService.findByProjectAssessmentID(projectAssessmentId);

    List<ExerciseAssessment> allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    for(ExerciseAssessment assessment : allAssessments) {
      if(assessment.getExercise().getMaxScore() == null){
        continue;
       }
      Integer assessmentID = assessment.getId();
      Float approvedScore = allScorings.stream()
                                       .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                       .map(CriteriaScoring::getApprovedScore).reduce(0.0f, Float::sum);
      Float submittedScore = allScorings.stream()
                                        .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                        .map(CriteriaScoring::getSubmittedScore).reduce(0.0f, Float::sum);
      if (approvedScore > (float) assessment.getExercise().getMaxScore()) {
        approvedScore = (float) assessment.getExercise().getMaxScore();
      }
      if (submittedScore > (float) assessment.getExercise().getMaxScore()) {
        submittedScore = (float) assessment.getExercise().getMaxScore();
      }
      assessment.setApprovedScore(approvedScore);
      assessment.setSubmittedScore(submittedScore);
      assessment = exerciseAssessmentService.save(assessment);
    }

    allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    Float approvedScore = allAssessments.stream()
                                       .map(ExerciseAssessment::getApprovedScore).reduce(0.0f, Float::sum);
    Float submittedScore = allAssessments.stream()
                                        .map(ExerciseAssessment::getSubmittedScore).reduce(0.0f, Float::sum);
    Float scoreModifier = allAssessments.stream()
                                        .map(ExerciseAssessment::getScoreModifier).reduce(0.0f, Float::sum);
    ProjectAssessment projectAssessment = projectAssessmentService.findById(projectAssessmentId);
    projectAssessment.setSubmittedScore(submittedScore);
    projectAssessment.setApprovedScore(approvedScore);
    projectAssessment.setScoreModifier(scoreModifier);
    projectAssessment = projectAssessmentService.save(projectAssessment);

    String json = new Gson().toJson(projectAssessment);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/criteria_scoring/{criteriaScoringId}/untake_score")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response uUntakeScore(@HeaderParam("Authorization") String authorization,
                                @PathParam("criteriaScoringId") Integer criteriaScoringId) {
    CriteriaScoring criteriaScoring = criteriaScoringService.findById(criteriaScoringId);

    criteriaScoring.setApprovalStatus(1);
    criteriaScoring.setSelected(false);
    if (criteriaScoring.getCriteria().getScore() != null) {
      criteriaScoring.setSubmittedScore(0.0f);
    }
    criteriaScoring = criteriaScoringService.save(criteriaScoring);
    ExerciseAssessment selectedExerciseAssessment = exerciseAssessmentService.findById(criteriaScoring.getExerciseAssessmentID());

    List<CriteriaScoring> criteriaScoringSelectedList = criteriaScoringService.findByExerciseAssessmentIDAndSelected(selectedExerciseAssessment.getId(), true);

    if (criteriaScoringSelectedList.size() == 0) {
      selectedExerciseAssessment.setScoreModifier(0.0f);
      exerciseAssessmentService.save(selectedExerciseAssessment);
    }

    Integer projectAssessmentId = criteriaScoring.getProjectAssessmentID();
    List<CriteriaScoring> allScorings = criteriaScoringService.findByProjectAssessmentID(projectAssessmentId);

    List<ExerciseAssessment> allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    for(ExerciseAssessment assessment : allAssessments) {
      if(assessment.getExercise().getMaxScore() == null){
        continue;
       }
      Integer assessmentID = assessment.getId();
      Float approvedScore = allScorings.stream()
                                       .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                       .map(CriteriaScoring::getApprovedScore).reduce(0.0f, Float::sum);
      Float submittedScore = allScorings.stream()
                                        .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                        .map(CriteriaScoring::getSubmittedScore).reduce(0.0f, Float::sum);
      if (approvedScore > (float) assessment.getExercise().getMaxScore()) {
        approvedScore = (float) assessment.getExercise().getMaxScore();
      }
      if (submittedScore > (float) assessment.getExercise().getMaxScore()) {
        submittedScore = (float) assessment.getExercise().getMaxScore();
      }
      assessment.setApprovedScore(approvedScore);
      assessment.setSubmittedScore(submittedScore);
      assessment = exerciseAssessmentService.save(assessment);
    }

    allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    Float approvedScore = allAssessments.stream()
                                       .map(ExerciseAssessment::getApprovedScore).reduce(0.0f, Float::sum);
    Float submittedScore = allAssessments.stream()
                                        .map(ExerciseAssessment::getSubmittedScore).reduce(0.0f, Float::sum);
    Float scoreModifier = allAssessments.stream()
                                        .map(ExerciseAssessment::getScoreModifier).reduce(0.0f, Float::sum);
    ProjectAssessment projectAssessment = projectAssessmentService.findById(projectAssessmentId);
    projectAssessment.setSubmittedScore(submittedScore);
    projectAssessment.setApprovedScore(approvedScore);
    projectAssessment.setScoreModifier(scoreModifier);
    projectAssessment = projectAssessmentService.save(projectAssessment);

    String json = new Gson().toJson(projectAssessment);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/criteria_scoring/{criteriaScoringId}/review")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response ReviewExercises(@HeaderParam("Authorization") String authorization,
                                @PathParam("criteriaScoringId") Integer criteriaScoringId,
                                @FormDataParam("approval_status") Integer approvalStatus,
                                @FormDataParam("approved_score") Float approveScore) {
    CriteriaScoring criteriaScoring = criteriaScoringService.findById(criteriaScoringId);
    
    criteriaScoring.setApprovalStatus(approvalStatus);
    criteriaScoring.setSelected(true);
    if (criteriaScoring.getCriteria().getScore() != null) {
      //rejected
      if (approvalStatus == 3) {
        criteriaScoring.setApprovedScore(0.0f);
        criteriaScoring.setSubmittedScore(0.0f);
      }
      //approved
      if (approvalStatus == 4) {
        if (criteriaScoring.getCriteria().getExerciseType().equals("max_score")) {
          criteriaScoring.setApprovedScore(approveScore);
        } else {
          criteriaScoring.setApprovedScore(criteriaScoring.getCriteria().getScore());
        }
        criteriaScoring.setSubmittedScore(0.0f);
      }
    }
    criteriaScoring = criteriaScoringService.save(criteriaScoring);
    
    Integer projectAssessmentId = criteriaScoring.getProjectAssessmentID();
    List<CriteriaScoring> allScorings = criteriaScoringService.findByProjectAssessmentID(projectAssessmentId);

    List<ExerciseAssessment> allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    for(ExerciseAssessment assessment : allAssessments) {
      if(assessment.getExercise().getMaxScore() == null){
        continue;
       }
      Integer assessmentID = assessment.getId();
      Float approvedScore = allScorings.stream()
                                       .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                       .map(CriteriaScoring::getApprovedScore).reduce(0.0f, Float::sum);
      Float submittedScore = allScorings.stream()
                                        .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                        .map(CriteriaScoring::getSubmittedScore).reduce(0.0f, Float::sum);
      if (approvedScore > (float) assessment.getExercise().getMaxScore()) {
        approvedScore = (float) assessment.getExercise().getMaxScore();
      }
      if (submittedScore > (float) assessment.getExercise().getMaxScore()) {
        submittedScore = (float) assessment.getExercise().getMaxScore();
      }
      assessment.setApprovedScore(approvedScore);
      assessment.setSubmittedScore(submittedScore);
      assessment = exerciseAssessmentService.save(assessment);
    }

    allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    Float approvedScore = allAssessments.stream()
                                       .map(ExerciseAssessment::getApprovedScore).reduce(0.0f, Float::sum);
    Float submittedScore = allAssessments.stream()
                                        .map(ExerciseAssessment::getSubmittedScore).reduce(0.0f, Float::sum);
    ProjectAssessment projectAssessment = projectAssessmentService.findById(projectAssessmentId);
    projectAssessment.setSubmittedScore(submittedScore);
    projectAssessment.setApprovedScore(approvedScore);
    projectAssessment = projectAssessmentService.save(projectAssessment);

    String json = new Gson().toJson(projectAssessment);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/assessment_attachment/{attachmentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAttachmentAssessmentUrlFile(@HeaderParam("Authorization") String authorization, 
    @PathParam("attachmentId") Integer attachmentId
  ) {
    ProjectAttachment attachment = projectAttachmentService.findById(attachmentId);
    String attachmentLink = attachment.getLink();

    String result;
    try {
      result = GetUrlGcs(attachmentLink);
    } catch (IOException e) {
      result = null;
      return Response.status(400).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result);
    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @DELETE
  @Path(value = "/assessment_attachment/{attachment_id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionAssessmentAttachmentDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("attachment_id") Integer attachmentId

  ) {
    boolean status = projectAttachmentService.deleteById(attachmentId);
    return Response.status(status ? 200 : 400).build();
  }

}
