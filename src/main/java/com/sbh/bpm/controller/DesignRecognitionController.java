package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.model.ProjectAssessment;
import com.sbh.bpm.service.GoogleCloudStorage;
import com.sbh.bpm.service.IAttachmentService;
import com.sbh.bpm.service.ICommentService;
import com.sbh.bpm.service.ICriteriaScoringService;
import com.sbh.bpm.service.IDocumentFileService;
import com.sbh.bpm.service.IExerciseAssessmentService;
import com.sbh.bpm.service.IMasterEvaluationService;
import com.sbh.bpm.service.IProjectAssessmentService;
import com.sbh.bpm.service.ITransactionCreationService;
import com.sbh.bpm.service.ITransactionFetchService;
import com.sbh.bpm.service.TransactionCreationService.TransactionCreationResponse;
import com.sbh.bpm.service.TransactionFetchService.TransactionFetchResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class DesignRecognitionController extends GcsUtil {
  private static final Logger logger = LogManager.getLogger(DesignRecognitionController.class);
  
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
  private ICommentService commentService;

  @Autowired
  private IAttachmentService attachmentService;
 
  @POST
  @Path(value = "/design_recognition")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionCreation(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
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

    TransactionCreationResponse response = transactionCreationService.createDRTransactionForProcessInstance(processInstanceId);

    String json = new Gson().toJson(response);
    return Response.status(200).entity(json).build();
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

    TransactionFetchResponse response = transactionFetchService.getDRTransactionForProcessInstance(processInstanceId);

    String json = new Gson().toJson(response);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/design_recognition/{criteriaScoringId}/take_score")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editExercises(@HeaderParam("Authorization") String authorization,
                                MasterExercise exercise, @PathParam("criteriaScoringId") Integer criteriaScoringId) {
    CriteriaScoring criteriaScoring = criteriaScoringService.findById(criteriaScoringId);
    criteriaScoring.setApprovalStatus(2);
    criteriaScoring.setSelected(true);
    criteriaScoring.setPotentialScore(criteriaScoring.getCriteria().getScore());
    criteriaScoring = criteriaScoringService.save(criteriaScoring);
    
    Integer projectAssessmentId = criteriaScoring.getProjectAssessmentID();
    List<CriteriaScoring> allScorings = criteriaScoringService.findByProjectAssessmentID(projectAssessmentId);
    Float potentialScore = allScorings.stream().map(CriteriaScoring::getPotentialScore).reduce(0.0f, Float::sum);
    Float score = allScorings.stream().map(CriteriaScoring::getScore).reduce(0.0f, Float::sum);

    ProjectAssessment projectAssessment = projectAssessmentService.findById(projectAssessmentId);
    projectAssessment.setTemporaryScore(score);
    projectAssessment.setPotentialScore(potentialScore);

    String json = new Gson().toJson(criteriaScoring);
    return Response.ok(json).build();
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
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceID(processInstanceId);

    String json = new Gson().toJson(projectAssessments);
    return Response.status(200).entity(json).build();
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
    comment.setCreatedAt(new Date());
    //change later
    comment.setRole("client");
    comment.setUserID("indofood1");
    comment = commentService.save(comment);

    String json = new Gson().toJson(comment);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/design_recognition/attachments")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionAttachmentCreation(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("document_id") Integer documentId

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

    List<Attachment> attachments = new ArrayList<Attachment>();

    try{
      List<String> filenames = new ArrayList<String>();
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        if (meta.getFileName() == null){
          continue;
         }
         if (filenames.contains(meta.getFileName())){
          continue;
         }

        BlobId blobId = UploadToGcs(activityInstanceId, is, meta);

        Attachment attachment = new Attachment();
        attachment.setCreatedAt(new Date());
        //change later
        attachment.setRole("client");
        attachment.setUploaderID("indofood1");
        attachment.setFilename(meta.getFileName());
        attachment.setLink(blobId.getName());
        attachment.setDocumentFileID(documentId);

        attachments.add(attachment);
        filenames.add(attachment.getFilename());
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    Iterable<Attachment> attachs = attachmentService.saveAll(attachments);
    
    String json = new Gson().toJson(attachs);
    return Response.status(200).entity(json).build();
  }

  @DELETE
  @Path(value = "/design_recognition/attachments/{attachmentId}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionAttachmentCreation(
    @HeaderParam("Authorization") String authorization,
    @PathParam("attachmentId") Integer attachmentId

  ) {
    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
    boolean status = attachmentService.deleteById(googleCloudStorage, attachmentId);
    return Response.status(status ? 200 : 400).build();
  }

  @GET
  @Path(value = "/design_recognition/{taskId}/evaluations")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterEvaluationByTaskID(@HeaderParam("Authorization") String authorization, @PathParam("taskId") String taskId) {      
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
 
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

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceID(processInstanceId);
    List<Integer> masterTemplateIds = projectAssessments.stream().map(ProjectAssessment::getMasterTemplateID).collect(Collectors.toList());

    List<MasterEvaluation> evaluations = masterEvaluationService.findByMasterTemplateIDIn(masterTemplateIds);

    String json = new Gson().toJson(evaluations);
    return Response.ok(json).build();
  }


}
