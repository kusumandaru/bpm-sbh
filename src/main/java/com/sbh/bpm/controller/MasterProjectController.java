package com.sbh.bpm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
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
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.model.MasterCriteriaBlocker;
import com.sbh.bpm.model.MasterDocument;
import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.model.MasterLevel;
import com.sbh.bpm.model.MasterTemplate;
import com.sbh.bpm.model.MasterVendor;
import com.sbh.bpm.service.IMasterCriteriaBlockerService;
import com.sbh.bpm.service.IMasterCriteriaService;
import com.sbh.bpm.service.IMasterDocumentService;
import com.sbh.bpm.service.IMasterEvaluationService;
import com.sbh.bpm.service.IMasterExerciseService;
import com.sbh.bpm.service.IMasterLevelService;
import com.sbh.bpm.service.IMasterTemplateService;
import com.sbh.bpm.service.IMasterVendorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/master-project")
public class MasterProjectController extends GcsUtil{
  private static final Logger logger = LogManager.getLogger(MasterProjectController.class);

  @Autowired
  private IMasterTemplateService masterTemplateService;

  @Autowired
  private IMasterVendorService masterVendorService;

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

  @GET
  @Path(value = "/levels")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allMasterLevel(@HeaderParam("Authorization") String authorization) {      
    List<MasterLevel> level = (List<MasterLevel>) masterLevelService.findAll();

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

  @POST
  @Path(value = "/exercises")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveExercises(@HeaderParam("Authorization") String authorization,
                                MasterExercise exercise) {
    exercise.setCreatedAt(new Date());                  
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
    masterExercise.setMaxScore(exercise.getMaxScore());

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

  @POST
  @Path(value = "/criterias")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveCriterias(@HeaderParam("Authorization") String authorization,
                                MasterCriteria criteria) {
    criteria.setCreatedAt(new Date());                  
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
    masterCriteria.setScore(criteria.getScore());
    masterCriteria.setAdditionalNotes(criteria.getAdditionalNotes());
    masterCriteria.setNotAvailable(criteria.getNotAvailable());

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

  @POST
  @Path(value = "/documents")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response saveDocuments(@HeaderParam("Authorization") String authorization,
                                MasterDocument document) {
    document.setCreatedAt(new Date());                  
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
    masterDocument.setMasterCriteriaID(document.getMasterCriteriaID());
    masterDocument.setName(document.getName());

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

}