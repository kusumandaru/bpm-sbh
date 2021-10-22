package com.sbh.bpm.controller;

import java.util.Date;
import java.util.List;

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
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.model.MasterCriteriaBlocker;
import com.sbh.bpm.model.MasterDocument;
import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.model.MasterTemplate;
import com.sbh.bpm.model.MasterVendor;
import com.sbh.bpm.service.IMasterCriteriaBlockerService;
import com.sbh.bpm.service.IMasterCriteriaService;
import com.sbh.bpm.service.IMasterDocumentService;
import com.sbh.bpm.service.IMasterEvaluationService;
import com.sbh.bpm.service.IMasterExerciseService;
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
  public Response allMasterTemplate(@HeaderParam("Authorization") String authorization) {      
    List<MasterTemplate> templates = (List<MasterTemplate>) masterTemplateService.findAll();

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

  @GET
  @Path(value = "/criterias/{criteria_id}/blockers")
  @Produces(MediaType.APPLICATION_JSON)
  public Response allCriteriaBlockersByCriteria(@HeaderParam("Authorization") String authorization, @PathParam("criteria_id") Integer criteriaID) { 
    List<MasterCriteriaBlocker> blockers = (List<MasterCriteriaBlocker>) masterCriteriaBlockerService.findBymasterCriteriaID(criteriaID);

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
  @Path(value = "/documents/{criteriaBlockerId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMasterDocument(@HeaderParam("Authorization") String authorization, @PathParam("criteriaBlockerId") Integer criteriaBlockerId) {      
    MasterDocument document = masterDocumentService.findById(criteriaBlockerId);

    String json = new Gson().toJson(document);
    return Response.ok(json).build();
  }
}