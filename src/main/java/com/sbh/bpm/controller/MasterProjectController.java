package com.sbh.bpm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.sbh.bpm.model.ActivityName;
import com.sbh.bpm.model.MasterCertificationType;
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.model.MasterCriteriaBlocker;
import com.sbh.bpm.model.MasterDocument;
import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.model.MasterLevel;
import com.sbh.bpm.model.MasterTemplate;
import com.sbh.bpm.model.MasterVendor;
import com.sbh.bpm.model.ProjectAssessment;
import com.sbh.bpm.service.IActivityNameService;
import com.sbh.bpm.service.IMasterCertificationTypeService;
import com.sbh.bpm.service.IMasterCriteriaBlockerService;
import com.sbh.bpm.service.IMasterCriteriaService;
import com.sbh.bpm.service.IMasterDocumentService;
import com.sbh.bpm.service.IMasterEvaluationService;
import com.sbh.bpm.service.IMasterExerciseService;
import com.sbh.bpm.service.IMasterLevelService;
import com.sbh.bpm.service.IMasterTemplateService;
import com.sbh.bpm.service.IMasterVendorService;
import com.sbh.bpm.service.IProjectAssessmentService;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/master-project")
public class MasterProjectController extends GcsUtil{
  // private static final Logger logger = LoggerFactory.getLogger(MasterProjectController.class);

  @Autowired
  private IMasterTemplateService masterTemplateService;

  @Autowired
  private IMasterVendorService masterVendorService;

  @Autowired
  private IMasterCertificationTypeService masterCertificationTypeService;

  @Autowired
  private IMasterCriteriaService masterCriteriaService;

  @Autowired
  private IMasterCriteriaBlockerService masterCriteriaBlockerService;

  @Autowired
  private IMasterDocumentService masterDocumentService;

  @Autowired
  private IMasterEvaluationService masterEvaluationService;

  @Autowired
  private IMasterExerciseService masterExerciseService;

  @Autowired
  private IMasterLevelService masterLevelService;

  @Autowired
  private IProjectAssessmentService projectAssessmentService;

  @Autowired
  private IActivityNameService activityNameService;

  @GET
  @Path(value = "/levels")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterLevel(@HeaderParam("Authorization") String authorization) {      
    List<MasterLevel> level = (List<MasterLevel>) masterLevelService.findAll();

    String json = new Gson().toJson(level);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/levels")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveLevels(@HeaderParam("Authorization") String authorization,
                                MasterLevel level) {
    level.setCreatedAt(new Date());                  
    level = masterLevelService.save(level);

    String json = new Gson().toJson(level);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/levels/{level_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response MasterLevelDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("level_id") Integer levelId

  ) {
    boolean status = masterLevelService.deleteById(levelId);
    return Response.status(status ? 200 : 400).build();
  }

  @GET
  @Path(value = "/levels/{task_id}/task/{assessment_type}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterLevelByTaskId(
    @HeaderParam("Authorization") String authorization,
    @PathParam("task_id") String taskId,
    @PathParam("assessment_type") String assessmentType
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

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceId, assessmentType);
    ProjectAssessment projectAssessment = projectAssessments.get(0);

    List<MasterLevel> level = (List<MasterLevel>) masterLevelService.findByMasterTemplateID(projectAssessment.getMasterTemplateID());

    String json = new Gson().toJson(level);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/levels/{levelId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterLevel(@HeaderParam("Authorization") String authorization, @PathParam("levelId") Integer levelId) {     
    MasterLevel level = masterLevelService.findById(levelId);

    String json = new Gson().toJson(level);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/levels/minimum_score")
  @Produces(MediaType.APPLICATION_JSON)
  public Response masterLevelMinimumScore(@HeaderParam("Authorization") String authorization) {      
    MasterLevel level = (MasterLevel) masterLevelService.findFirstByOrderByMinimumScoreAsc();

    String json = new Gson().toJson(level);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/levels/{levelId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editLevels(@HeaderParam("Authorization") String authorization,
                                MasterLevel level, @PathParam("levelId") Integer levelId) {
    MasterLevel masterLevel = masterLevelService.findById(levelId);
    masterLevel.setMasterTemplateID(level.getMasterTemplateID());
    masterLevel.setName(level.getName());
    masterLevel.setMinimumScore(level.getMinimumScore());
    masterLevel.setPercentage(level.getPercentage());
    masterLevel.setActive(level.getActive());
    masterLevel = masterLevelService.save(masterLevel);

    String json = new Gson().toJson(masterLevel);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/vendors")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterVendor(@HeaderParam("Authorization") String authorization) {      
    List<MasterVendor> vendors = (List<MasterVendor>) masterVendorService.findAll();

    String json = new Gson().toJson(vendors);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/vendors/{vendorId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterVendor(@HeaderParam("Authorization") String authorization, @PathParam("vendorId") Integer vendorId) {      
    MasterVendor vendor = masterVendorService.findById(vendorId);

    String json = new Gson().toJson(vendor);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/vendors/{vendor_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response VendorDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("vendor_id") Integer vendorId
  ) {
    List<MasterCertificationType> templates = (List<MasterCertificationType>) masterCertificationTypeService.findByMasterVendorID(vendorId);
    if(templates.size()>0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Cannot delete, there is any certification type refer this subject, delete them first");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    boolean status = masterVendorService.deleteById(vendorId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/vendors")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveVendors(@HeaderParam("Authorization") String authorization,
                                MasterVendor vendor) {
    vendor.setCreatedAt(new Date());               
    vendor = masterVendorService.save(vendor);

    String json = new Gson().toJson(vendor);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/vendors/{vendorId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editVendors(@HeaderParam("Authorization") String authorization,
                                MasterVendor vendor, @PathParam("vendorId") Integer vendorId) {
    MasterVendor masterVendor = masterVendorService.findById(vendorId);
    masterVendor.setVendorCode(vendor.getVendorCode());
    masterVendor.setDescription(vendor.getDescription());
    masterVendor.setVendorName(vendor.getVendorName());
    masterVendor.setActive(vendor.getActive());

    masterVendor = masterVendorService.save(masterVendor);

    String json = new Gson().toJson(masterVendor);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/vendors/{vendor_id}/templates")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allTemplatesByVendor(@HeaderParam("Authorization") String authorization, @PathParam("vendor_id") Integer vendorID) { 
    List<MasterTemplate> templates = (List<MasterTemplate>) masterTemplateService.findByMasterVendorID(vendorID);

    String json = new Gson().toJson(templates);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/vendors/{vendor_id}/certification_types")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AllCertificationTypesByVendor(@HeaderParam("Authorization") String authorization, @PathParam("vendor_id") Integer vendorID) { 
    List<MasterCertificationType> templates = (List<MasterCertificationType>) masterCertificationTypeService.findByMasterVendorID(vendorID);

    String json = new Gson().toJson(templates);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/certification_types")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AllMasterCertificationType(@HeaderParam("Authorization") String authorization, @Context UriInfo info) {
    String certificationCode = info.getQueryParameters().getFirst("certidication_code");
    List<MasterCertificationType> certificationTypes = new ArrayList<>();
    if (certificationCode != null && !certificationCode.isEmpty()) {
      certificationTypes = (List<MasterCertificationType>) masterCertificationTypeService.findByCertificationCode(certificationCode);
    } else {
      certificationTypes = (List<MasterCertificationType>) masterCertificationTypeService.findAll();
    }

    String json = new Gson().toJson(certificationTypes);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/certification_types/{certificationTypeId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterCertificationType(@HeaderParam("Authorization") String authorization, @PathParam("certificationTypeId") Integer certificationTypeId) {      
    MasterCertificationType certificationType = masterCertificationTypeService.findById(certificationTypeId);

    String json = new Gson().toJson(certificationType);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/certification_types/{certification_type_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CertificationTypeDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("certification_type_id") Integer certificationTypeId
  ) {
    List<MasterTemplate> templates = (List<MasterTemplate>) masterTemplateService.findByMasterCertificationTypeID(certificationTypeId);
    if(templates.size()>0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Cannot delete, there is any master template refer this subject, delete them first");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    
    boolean status = masterCertificationTypeService.deleteById(certificationTypeId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/certification_types")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response SaveCertificationTypes(@HeaderParam("Authorization") String authorization,
                                MasterCertificationType certificationType) {
    certificationType.setCreatedAt(new Date());                  
    certificationType = masterCertificationTypeService.save(certificationType);

    String json = new Gson().toJson(certificationType);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/certification_types/{certificationTypeId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response EditCertificationTypes(@HeaderParam("Authorization") String authorization,
                                MasterCertificationType certificationType, @PathParam("certificationTypeId") Integer certificationTypeId) {
    MasterCertificationType masterCertificationType = masterCertificationTypeService.findById(certificationTypeId);
    masterCertificationType.setMasterVendorID(certificationType.getMasterVendorID());
    masterCertificationType.setCertificationCode(certificationType.getCertificationCode());
    masterCertificationType.setCertificationName(certificationType.getCertificationName());
    masterCertificationType.setActive(certificationType.getActive());

    masterCertificationType = masterCertificationTypeService.save(masterCertificationType);

    String json = new Gson().toJson(masterCertificationType);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/certification_types/{certification_type_id}/templates")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AllTemplatesByCertificationType(@HeaderParam("Authorization") String authorization, @PathParam("certification_type_id") Integer certificationTypeID) { 
    List<MasterTemplate> templates = (List<MasterTemplate>) masterTemplateService.findByMasterCertificationTypeID(certificationTypeID);

    String json = new Gson().toJson(templates);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/templates")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterTemplate(@HeaderParam("Authorization") String authorization, @Context UriInfo info) {
    String projectType = info.getQueryParameters().getFirst("project_type");
    List<MasterTemplate> templates = new ArrayList<>();
    if (projectType != null && !projectType.isEmpty()) {
      templates = (List<MasterTemplate>) masterTemplateService.findByProjectType(projectType);
    } else {
      templates = (List<MasterTemplate>) masterTemplateService.findAll();
    }

    String json = new Gson().toJson(templates);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/templates/{templateId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterTemplate(@HeaderParam("Authorization") String authorization, @PathParam("templateId") Integer templateId) {      
    MasterTemplate template = masterTemplateService.findById(templateId);

    String json = new Gson().toJson(template);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/templates/{template_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response TemplateDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("template_id") Integer templateId
  ) {
    List<MasterEvaluation> evaluations = (List<MasterEvaluation>) masterEvaluationService.findByMasterTemplateID(templateId);
    if(evaluations.size()>0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Cannot delete, there is any master evaluation refer this subject, delete them first");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    List<MasterLevel> levels = (List<MasterLevel>) masterLevelService.findByMasterTemplateID(templateId);
    if(levels.size()>0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Cannot delete, there is any master level refer this subject, delete them first");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    
    boolean status = masterTemplateService.deleteById(templateId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/templates")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveTemplates(@HeaderParam("Authorization") String authorization,
                                MasterTemplate template) {
    template.setCreatedAt(new Date());                  
    template = masterTemplateService.save(template);

    String json = new Gson().toJson(template);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/templates/{templateId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editTemplates(@HeaderParam("Authorization") String authorization,
                                MasterTemplate template, @PathParam("templateId") Integer templateId) {
    MasterTemplate masterTemplate = masterTemplateService.findById(templateId);
    masterTemplate.setMasterVendorID(template.getMasterVendorID());
    masterTemplate.setProjectType(template.getProjectType());
    masterTemplate.setProjectVersion(template.getProjectVersion());
    masterTemplate.setActive(template.getActive());

    masterTemplate = masterTemplateService.save(masterTemplate);

    String json = new Gson().toJson(masterTemplate);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/templates/{template_id}/evaluations")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allEvaluationsByExercise(@HeaderParam("Authorization") String authorization, @PathParam("template_id") Integer templateID) { 
    List<MasterEvaluation> evaluations = (List<MasterEvaluation>) masterEvaluationService.findByMasterTemplateID(templateID);

    String json = new Gson().toJson(evaluations);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/templates/{template_id}/levels")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allLevelsByExercise(@HeaderParam("Authorization") String authorization, @PathParam("template_id") Integer templateID) { 
    List<MasterLevel> levels = (List<MasterLevel>) masterLevelService.findByMasterTemplateID(templateID);

    String json = new Gson().toJson(levels);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/evaluations")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterEvaluation(@HeaderParam("Authorization") String authorization) {      
    List<MasterEvaluation> evaluations = (List<MasterEvaluation>) masterEvaluationService.findAll();

    String json = new Gson().toJson(evaluations);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/evaluations/{evaluationId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterEvaluation(@HeaderParam("Authorization") String authorization, @PathParam("evaluationId") Integer evaluationId) {      
    MasterEvaluation evaluation = masterEvaluationService.findById(evaluationId);

    String json = new Gson().toJson(evaluation);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/evaluations/{evaluation_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response EvaluationDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("evaluation_id") Integer evaluationId
  ) {
    List<MasterExercise> exercises = (List<MasterExercise>) masterExerciseService.findByMasterEvaluationID(evaluationId);
    if(exercises.size()>0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Cannot delete, there is any master exercise refer this subject, delete them first");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    
    boolean status = masterEvaluationService.deleteById(evaluationId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/evaluations")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveEvaluations(@HeaderParam("Authorization") String authorization,
                                MasterEvaluation evaluation) {
    evaluation.setCreatedAt(new Date());                  
    evaluation = masterEvaluationService.save(evaluation);

    String json = new Gson().toJson(evaluation);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/evaluations/{evaluationId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editEvaluations(@HeaderParam("Authorization") String authorization,
                                MasterEvaluation evaluation, @PathParam("evaluationId") Integer evaluationId) {
    MasterEvaluation masterEvaluation = masterEvaluationService.findById(evaluationId);
    masterEvaluation.setMasterTemplateID(evaluation.getMasterTemplateID());
    masterEvaluation.setCode(evaluation.getCode());
    masterEvaluation.setName(evaluation.getName());
    masterEvaluation.setActive(evaluation.getActive());

    masterEvaluation = masterEvaluationService.save(masterEvaluation);

    String json = new Gson().toJson(masterEvaluation);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/evaluations/{evaluation_id}/exercises")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allExercisesByEvaluation(@HeaderParam("Authorization") String authorization, @PathParam("evaluation_id") Integer evaluationID) { 
    List<MasterExercise> exercises = (List<MasterExercise>) masterExerciseService.findByMasterEvaluationID(evaluationID);

    String json = new Gson().toJson(exercises);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/exercises")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterExercise(@HeaderParam("Authorization") String authorization) {      
    List<MasterExercise> exercises = (List<MasterExercise>) masterExerciseService.findAll();

    String json = new Gson().toJson(exercises);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/exercises/{exerciseId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterExercise(@HeaderParam("Authorization") String authorization, @PathParam("exerciseId") Integer exerciseId) {      
    MasterExercise exercise = masterExerciseService.findById(exerciseId);

    String json = new Gson().toJson(exercise);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/exercises/{exercise_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ExerciseDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("exercise_id") Integer exerciseId
  ) {
    List<MasterCriteria> criterias = (List<MasterCriteria>) masterCriteriaService.findByMasterExerciseID(exerciseId);
    if(criterias.size()>0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Cannot delete, there is any master criteria refer this subject, delete them first");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    
    boolean status = masterExerciseService.deleteById(exerciseId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/exercises")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveExercises(@HeaderParam("Authorization") String authorization,
                                MasterExercise exercise) {
    exercise.setCreatedAt(new Date());   
    if (exercise.getExerciseType().equals("prequisite")) {
      exercise.setMaxScore(null);
    }
    exercise = masterExerciseService.save(exercise);

    String json = new Gson().toJson(exercise);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/exercises/{exerciseId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editExercises(@HeaderParam("Authorization") String authorization,
                                MasterExercise exercise, @PathParam("exerciseId") Integer exerciseId) {
    MasterExercise masterExercise = masterExerciseService.findById(exerciseId);
    masterExercise.setMasterEvaluationID(exercise.getMasterEvaluationID());
    masterExercise.setExerciseType(exercise.getExerciseType());
    masterExercise.setCode(exercise.getCode());
    masterExercise.setName(exercise.getName());
    masterExercise.setScoreModifier(exercise.getScoreModifier());
    if (masterExercise.getExerciseType().equals("prequisite")) {
      masterExercise.setMaxScore(null);
    } else {
      masterExercise.setMaxScore(exercise.getMaxScore());
    }
    masterExercise.setActive(exercise.getActive());

    masterExercise = masterExerciseService.save(masterExercise);

    String json = new Gson().toJson(masterExercise);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/exercises/{exercise_id}/criterias")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allCriteriasByExercise(@HeaderParam("Authorization") String authorization, @PathParam("exercise_id") Integer exerciseID) { 
    List<MasterCriteria> criterias = (List<MasterCriteria>) masterCriteriaService.findByMasterExerciseID(exerciseID);

    String json = new Gson().toJson(criterias);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/criterias")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterCriteria(@HeaderParam("Authorization") String authorization) {      
    List<MasterCriteria> criterias = (List<MasterCriteria>) masterCriteriaService.findAll();

    String json = new Gson().toJson(criterias);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/criterias/{criteriaId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterCriteria(@HeaderParam("Authorization") String authorization, @PathParam("criteriaId") Integer criteriaId) {      
    MasterCriteria criteria = masterCriteriaService.findById(criteriaId);

    String json = new Gson().toJson(criteria);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/criterias/{criteria_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CriteriaDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("criteria_id") Integer criteriaId
  ) {
    List<MasterCriteriaBlocker> blockers = (List<MasterCriteriaBlocker>) masterCriteriaBlockerService.findBymasterCriteriaID(criteriaId);
    if(blockers.size()>0) {
      return Response.status(400, "Cannot delete, there is any master blocker refer this subject, delete them first").build();
    }

    List<MasterDocument> documents = (List<MasterDocument>) masterDocumentService.findBymasterCriteriaID(criteriaId);
    if(documents.size()>0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Cannot delete, there is any master document refer this subject, delete them first");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    
    boolean status = masterCriteriaService.deleteById(criteriaId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/criterias")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCriterias(@HeaderParam("Authorization") String authorization,
                                MasterCriteria criteria) {
    criteria.setCreatedAt(new Date());
    if (criteria.getExerciseType().equals("prequisite")) {
      criteria.setScore(null);
    }               
    criteria = masterCriteriaService.save(criteria);

    String json = new Gson().toJson(criteria);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/criterias/{criteriaId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editCriterias(@HeaderParam("Authorization") String authorization,
                                MasterCriteria criteria, @PathParam("criteriaId") Integer criteriaId) {
    MasterCriteria masterCriteria = masterCriteriaService.findById(criteriaId);
    masterCriteria.setMasterExerciseID(criteria.getMasterExerciseID());
    masterCriteria.setExerciseType(criteria.getExerciseType());
    masterCriteria.setCode(criteria.getCode());
    masterCriteria.setDescription(criteria.getDescription());
    if (masterCriteria.getExerciseType().equals("prequisite")) {
      masterCriteria.setScore(null);
    } else {
      masterCriteria.setScore(criteria.getScore());
    }
    masterCriteria.setScore(criteria.getScore());
    masterCriteria.setAdditionalNotes(criteria.getAdditionalNotes());
    masterCriteria.setNotAvailable(criteria.getNotAvailable());
    masterCriteria.setActive(criteria.getActive());

    masterCriteria = masterCriteriaService.save(masterCriteria);

    String json = new Gson().toJson(masterCriteria);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/criterias/{criteria_id}/blockers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allCriteriaBlockersByCriteria(@HeaderParam("Authorization") String authorization, @PathParam("criteria_id") Integer criteriaID) { 
    List<MasterCriteriaBlocker> blockers = (List<MasterCriteriaBlocker>) masterCriteriaBlockerService.findBymasterCriteriaID(criteriaID);

    String json = new Gson().toJson(blockers);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/criterias/{criteria_id}/outside") 
  @Produces(MediaType.APPLICATION_JSON)
  public Response allCriteriasByCriteriaIDSameExerciseExceptSelf(@HeaderParam("Authorization") String authorization, @PathParam("criteria_id") Integer criteriaID) { 
    List<MasterCriteria> blockers = (List<MasterCriteria>) masterCriteriaService.withoutSelfSameExercise(criteriaID);

    String json = new Gson().toJson(blockers);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/criteria_blockers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterCriteriaBlocker(@HeaderParam("Authorization") String authorization) {      
    List<MasterCriteriaBlocker> criteria_blockers = (List<MasterCriteriaBlocker>) masterCriteriaBlockerService.findAll();

    String json = new Gson().toJson(criteria_blockers);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/criteria_blockers/{criteriaBlockerId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterCriteriaBlocker(@HeaderParam("Authorization") String authorization, @PathParam("criteriaBlockerId") Integer criteriaBlockerId) {      
    MasterCriteriaBlocker criteria_blocker = masterCriteriaBlockerService.findById(criteriaBlockerId);

    String json = new Gson().toJson(criteria_blocker);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value =  "/criteria_blockers/{criteria_blocker_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response CriteriaBlockerDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("criteria_blocker_id") Integer criteriaBlockerId
  ) {    
    boolean status = masterCriteriaBlockerService.deleteById(criteriaBlockerId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/criteria_blockers")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCriteriaBlockers(@HeaderParam("Authorization") String authorization,
                                MasterCriteriaBlocker criteria_blocker) {
    criteria_blocker.setCreatedAt(new Date());                  
    criteria_blocker = masterCriteriaBlockerService.save(criteria_blocker);

    String json = new Gson().toJson(criteria_blocker);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/criteria_blockers/{criteria_blockerId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editCriteriaBlockers(@HeaderParam("Authorization") String authorization,
                                MasterCriteriaBlocker criteria_blocker, @PathParam("criteria_blockerId") Integer criteria_blockerId) {
    MasterCriteriaBlocker masterCriteriaBlocker = masterCriteriaBlockerService.findById(criteria_blockerId);
    masterCriteriaBlocker.setMasterCriteriaID(criteria_blocker.getMasterCriteriaID());
    masterCriteriaBlocker.setBlockerID(criteria_blocker.getBlockerID());
    masterCriteriaBlocker.setActive(criteria_blocker.getActive());

    masterCriteriaBlocker = masterCriteriaBlockerService.save(masterCriteriaBlocker);

    String json = new Gson().toJson(masterCriteriaBlocker);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/criterias/{criteria_id}/documents")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allDocumentsByCriteria(@HeaderParam("Authorization") String authorization, @PathParam("criteria_id") Integer criteriaID) { 
    List<MasterDocument> documents = (List<MasterDocument>) masterDocumentService.findBymasterCriteriaID(criteriaID);

    String json = new Gson().toJson(documents);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/documents")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterDocument(@HeaderParam("Authorization") String authorization) {      
    List<MasterDocument> documents = (List<MasterDocument>) masterDocumentService.findAll();

    String json = new Gson().toJson(documents);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/documents/{documentId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterDocument(@HeaderParam("Authorization") String authorization, @PathParam("documentId") Integer documentId) {      
    MasterDocument document = masterDocumentService.findById(documentId);

    String json = new Gson().toJson(document);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/documents/{document_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("document_id") Integer documentId
  ) {    
    boolean status = masterDocumentService.deleteById(documentId);
    return Response.status(status ? 200 : 400).build();
  }

  @POST
  @Path(value = "/documents")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveDocuments(@HeaderParam("Authorization") String authorization,
                                MasterDocument document) {
    document.setCreatedAt(new Date());
    MasterCriteria criteria = masterCriteriaService.findById(document.getMasterCriteriaID());
    document.setCriteriaCode(criteria.getCode());                
    document = masterDocumentService.save(document);

    String json = new Gson().toJson(document);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/documents/{documentId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response editDocuments(@HeaderParam("Authorization") String authorization,
                                MasterDocument document, @PathParam("documentId") Integer documentId) {
    MasterDocument masterDocument = masterDocumentService.findById(documentId);
    MasterCriteria criteria = masterCriteriaService.findById(document.getMasterCriteriaID());
    masterDocument.setCriteriaCode(criteria.getCode());    
    masterDocument.setMasterCriteriaID(document.getMasterCriteriaID());
    masterDocument.setName(document.getName());
    masterDocument.setActive(document.getActive());

    masterDocument = masterDocumentService.save(masterDocument);

    String json = new Gson().toJson(masterDocument);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/correlation_criteria_blockers/{criteria_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response updateCriteriaBlockerCorrelations(@HeaderParam("Authorization") String authorization, 
                                                    @FormParam("blocker_ids") String blockerIds, 
                                                    @PathParam("criteria_id") Integer criteriaId) {
    masterCriteriaBlockerService.deleteBymasterCriteriaID(criteriaId);
    for (String blockerId : blockerIds.split(",")) {
      MasterCriteriaBlocker masterCriteriaBlocker = new MasterCriteriaBlocker();
      masterCriteriaBlocker.setMasterCriteriaID(criteriaId);
      masterCriteriaBlocker.setBlockerID(Integer.parseInt(blockerId));
      masterCriteriaBlocker.setCreatedAt(new Date());                
      masterCriteriaBlocker = masterCriteriaBlockerService.save(masterCriteriaBlocker);
    }
    
    List<MasterCriteriaBlocker> blockers = masterCriteriaBlockerService.findBymasterCriteriaID(criteriaId);
    String json = new Gson().toJson(blockers);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/activity_names")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllActivityName(@HeaderParam("Authorization") String authorization) {
    List<ActivityName> activityNames = activityNameService.findAll();

    String json = new Gson().toJson(activityNames);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/activity_names/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActivityNameDetail(@HeaderParam("Authorization") String authorization,
    @PathParam("id") Integer id
  ) {
    ActivityName activityName = activityNameService.findById(id);

    String json = new Gson().toJson(activityName);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/activity_names/{master_certification_type_id}/activity/{activity_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActivityNameByActivityId(@HeaderParam("Authorization") String authorization,
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID,
    @PathParam("activity_id") String activityID

  ) {
    List<ActivityName> activityNames = activityNameService.findByMasterCertificationTypeIDAndActivityID(masterCertificationTypeID, activityID);

    String json = new Gson().toJson(activityNames);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/active_activity_names/{master_certification_type_id}/master_certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveAcitivityName(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ActivityName> activityNames = activityNameService.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);

    String json = new Gson().toJson(activityNames);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/activity_names/{master_certification_type_id}/master_certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GeActivityNameByMasterCertificationType(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ActivityName> activityNames = activityNameService.findByMasterCertificationTypeID(masterCertificationTypeID);

    String json = new Gson().toJson(activityNames);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/activity_names")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response ActivityNameCreation(
    @HeaderParam("Authorization") String authorization,
    ActivityName activityName
  ) {
    activityName.setCreatedAt(new Date()); 
    activityName.setCreatedBy("system");       
    activityName = activityNameService.save(activityName);

    String json = new Gson().toJson(activityName);
    return Response.status(200).entity(json).build();
  }

  @PATCH
  @Path(value = "/activity_names/{activity_name_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updatActivityName(@HeaderParam("Authorization") String authorization,
                               @PathParam("activity_name_id") Integer activityNameId, 
                               ActivityName activityName) {   
    ActivityName act = (ActivityName) activityNameService.findById(activityNameId);
    if (act == null) {
      return Response.status(400, "project document activityName not found").build();
    }

    act.setName(activityName.getName());
    act.setActivityID(activityName.getActivityID());
    act.setActive(activityName.getActive());
    act.setMasterCertificationTypeID(activityName.getMasterCertificationTypeID());
    act.setCreatedBy(activityName.getCreatedBy());

    act = activityNameService.save(act);

    String json = new Gson().toJson(act);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/activity_names/{activity_name_id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response ActivityNameDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("activity_name_id") Integer activityNameId

  ) {
    boolean status = activityNameService.deleteById(activityNameId);
    return Response.status(status ? 200 : 400).build();
  }
}