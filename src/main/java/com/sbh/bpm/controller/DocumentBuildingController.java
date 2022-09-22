package com.sbh.bpm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.sbh.bpm.model.ProjectDocumentBuilding;
import com.sbh.bpm.model.ProjectDocumentCategory;
import com.sbh.bpm.model.ProjectDocumentGenerate;
import com.sbh.bpm.service.IProjectDocumentBuildingService;
import com.sbh.bpm.service.IProjectDocumentCategoryService;
import com.sbh.bpm.service.IProjectDocumentGenerateService;

import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class DocumentBuildingController extends GcsUtil {
  // private static final Logger logger = LoggerFactory.getLogger(DocumentBuildingController.class);

  @Autowired
  private IProjectDocumentBuildingService projectDocumentBuildingService;

  @Autowired
  private IProjectDocumentGenerateService projectDocumentGenerateService;

  @Autowired
  private IProjectDocumentCategoryService projectDocumentCategoryService;

  @GET
  @Path(value = "/project/document_buildings")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllDocumentBuilding(@HeaderParam("Authorization") String authorization) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findAll();

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_generates")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllDocumentGenerate(@HeaderParam("Authorization") String authorization) {
    List<ProjectDocumentGenerate> documentGenerates = projectDocumentGenerateService.findAll();

    String json = new Gson().toJson(documentGenerates);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/generate_building_categories")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllDocumentGenerateCategory(@HeaderParam("Authorization") String authorization) {
    HashMap<String, String> hm = new HashMap<String, String>();
    hm.put("proof_of_payment", "Proof of Payment");
    hm.put("first_payment_document", "First Payment");
    hm.put("second_payment_document", "Second Payment");
    hm.put("third_payment_document", "Third Payment");
    hm.put("workshop_attendance_document", "Workshop Attendance");
    hm.put("workshop_report_document", "Workshop Report");
    hm.put("eligibility_statement", "Eligibility Statement");
    hm.put("agreement_letter_document", "Agreement Letter");
    hm.put("sign_post", "Sign Post");
    hm.put("approval_building_release", "Approval Building Release");
    hm.put("dr_scoring_form", "Design Recognition Scoring Form");
    hm.put("fa_scoring_form", "Final Assessment Scoring Form");
    hm.put("dr_revision_submission", "Design Recognition Revision Submission");
    hm.put("fa_revision_submission", "Final Assessment Revision Submission");
    hm.put("dr_evaluation_assessment", "Design Recognition Evaluation Assessment");
    hm.put("fa_evaluation_assessment", "Final Assessment Evaluation Assessment");

    List<ProjectDocumentBuilding> documentBuildings = new ArrayList<>();

    hm.forEach((key, value) -> {
      ProjectDocumentBuilding documentBuilding = new ProjectDocumentBuilding(key, value);
      documentBuildings.add(documentBuilding);
    });

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_building_categories")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllDocumentBuildingCategory(@HeaderParam("Authorization") String authorization) {
    List<ProjectDocumentCategory> documentBuildingCategories = projectDocumentCategoryService.findAll();

    String json = new Gson().toJson(documentBuildingCategories);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_buildings/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDocumentBuildingDetail(@HeaderParam("Authorization") String authorization,
    @PathParam("id") Integer id
  ) {
    ProjectDocumentBuilding documentBuilding = projectDocumentBuildingService.findById(id);

    String json = new Gson().toJson(documentBuilding);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_generates/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDocumentGenerateDetail(@HeaderParam("Authorization") String authorization,
    @PathParam("id") Integer id
  ) {
    ProjectDocumentGenerate documentGenerate = projectDocumentGenerateService.findById(id);

    String json = new Gson().toJson(documentGenerate);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_building_categories/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDocumentBuildingCategoryDetail(@HeaderParam("Authorization") String authorization,
    @PathParam("id") Integer id
  ) {
    ProjectDocumentCategory documentBuildingCategory = projectDocumentCategoryService.findById(id);

    String json = new Gson().toJson(documentBuildingCategory);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/active_document_buildings/{master_certification_type_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentBuilding(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/active_document_generates/{master_certification_type_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentGenerate(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ProjectDocumentGenerate> documentGenerates = projectDocumentGenerateService.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);

    String json = new Gson().toJson(documentGenerates);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_buildings/{master_certification_type_id}/master_certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDocumentBuildingByMasterCertificationType(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findByMasterCertificationTypeID(masterCertificationTypeID);

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_generates/{master_certification_type_id}/master_certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDocumentGenerateByMasterCertificationType(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ProjectDocumentGenerate> documentGenerates = projectDocumentGenerateService.findByMasterCertificationTypeID(masterCertificationTypeID);

    String json = new Gson().toJson(documentGenerates);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/active_document_buildings/{master_certification_type_id}/master_certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentBuildingByMasterCertificationType(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/active_document_generates/{master_certification_type_id}/master_certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentGenerateByMasterCertificationType(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_certification_type_id") Integer masterCertificationTypeID
  ) {
    List<ProjectDocumentGenerate> documentGenerates = projectDocumentGenerateService.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);

    String json = new Gson().toJson(documentGenerates);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/active_document_buildings/{certification_type_id}/certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentBuildingByCertificationType(@HeaderParam("Authorization") String authorization, 
    @PathParam("certification_type_id") Integer certificationTypeID
  ) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findByMasterCertificationTypeIDAndActiveTrue(certificationTypeID);

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/active_document_generates/{certification_type_id}/certification_type")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentGenerateByCertificationType(@HeaderParam("Authorization") String authorization, 
    @PathParam("certification_type_id") Integer certificationTypeID
  ) {
    List<ProjectDocumentGenerate> documentGenerates = projectDocumentGenerateService.findByMasterCertificationTypeIDAndActiveTrue(certificationTypeID);

    String json = new Gson().toJson(documentGenerates);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/project/document_buildings")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentBuildingCreation(
    @HeaderParam("Authorization") String authorization,
    ProjectDocumentBuilding building
  ) {
    building.setCreatedAt(new Date()); 
    building.setCreatedBy("system");       
    building = projectDocumentBuildingService.save(building);

    String json = new Gson().toJson(building);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/project/document_generates")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentGenerateCreation(
    @HeaderParam("Authorization") String authorization,
    ProjectDocumentGenerate document
  ) {
    document.setCreatedAt(new Date());    
    document.setCreatedBy("system");        
    document = projectDocumentGenerateService.save(document);

    String json = new Gson().toJson(document);
    return Response.status(200).entity(json).build();
  }

  @POST
  @Path(value = "/project/document_building_categories")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentBuildingCategoryCreation(
    @HeaderParam("Authorization") String authorization,
    ProjectDocumentCategory buildingCategory
  ) {
    buildingCategory.setCreatedAt(new Date());
    buildingCategory.setActive(true);    
    buildingCategory.setCreatedBy("system");          
    buildingCategory = projectDocumentCategoryService.save(buildingCategory);

    String json = new Gson().toJson(buildingCategory);
    return Response.status(200).entity(json).build();
  }

  @PATCH
  @Path(value = "/project/document_buildings/{document_building_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateBuildingProject(@HeaderParam("Authorization") String authorization,
                               @PathParam("document_building_id") Integer documentBuildingId, 
                               ProjectDocumentBuilding building) {   
    ProjectDocumentBuilding pdb = (ProjectDocumentBuilding) projectDocumentBuildingService.findById(documentBuildingId);
    if (pdb == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "project document building not found");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json.toString()).build();
    }

    pdb.setName(building.getName());
    pdb.setCode(building.getCode());
    pdb.setPlaceholder(building.getPlaceholder());
    pdb.setObjectType(building.getObjectType());
    pdb.setMandatory(building.getMandatory());
    pdb.setActive(building.getActive());
    pdb.setProjectDocumentCategoryID(building.getProjectDocumentCategoryID());
    pdb.setCreatedBy(building.getCreatedBy());

    pdb = projectDocumentBuildingService.save(pdb);

    String json = new Gson().toJson(pdb);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/project/document_generates/{document_generate_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateGenerateProject(@HeaderParam("Authorization") String authorization,
                               @PathParam("document_generate_id") Integer documentGenerateId, 
                               ProjectDocumentGenerate document) {   
    ProjectDocumentGenerate pdg = (ProjectDocumentGenerate) projectDocumentGenerateService.findById(documentGenerateId);
    if (pdg == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "project document generate not found");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json.toString()).build();
    }

    pdg.setName(document.getName());
    pdg.setCode(document.getCode());
    pdg.setActive(document.getActive());
    pdg.setProjectDocumentCategoryID(document.getProjectDocumentCategoryID());
    pdg.setCreatedBy(document.getCreatedBy());

    pdg = projectDocumentGenerateService.save(pdg);

    String json = new Gson().toJson(pdg);
    return Response.ok(json).build();
  }

  @PATCH
  @Path(value = "/project/document_building_categories/{category_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateBuildingProjectCategory(@HeaderParam("Authorization") String authorization,
                               @PathParam("category_id") Integer categoryId, 
                               ProjectDocumentCategory buildingCategory) {   
    ProjectDocumentCategory pdbc = (ProjectDocumentCategory) projectDocumentCategoryService.findById(categoryId);
    if (pdbc == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "project document building category not found");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json.toString()).build();
    }

    pdbc.setName(buildingCategory.getName());
    pdbc.setCode(buildingCategory.getCode());
    pdbc.setDescription(buildingCategory.getDescription());
    pdbc.setActive(buildingCategory.getActive());
    pdbc.setCreatedBy(buildingCategory.getCreatedBy());

    pdbc = projectDocumentCategoryService.save(pdbc);

    String json = new Gson().toJson(pdbc);
    return Response.ok(json).build();
  }

  @DELETE
  @Path(value = "/project/document_buildings/{document_building_id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentBuildingDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("document_building_id") Integer documentBuildingId

  ) {
    boolean status = projectDocumentBuildingService.deleteById(documentBuildingId);
    return Response.status(status ? 200 : 400).build();
  }

  @DELETE
  @Path(value = "/project/document_generates/{document_generate_id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentGenerateDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("document_generate_id") Integer documentGenerateId

  ) {
    boolean status = projectDocumentGenerateService.deleteById(documentGenerateId);
    return Response.status(status ? 200 : 400).build();
  }

  @DELETE
  @Path(value = "/project/document_building_categories/{category_id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response DocumentBuildingCategoryDeletion(
    @HeaderParam("Authorization") String authorization,
    @PathParam("category_id") Integer categoryID

  ) {
    boolean status = projectDocumentCategoryService.deleteById(categoryID);
    return Response.status(status ? 200 : 400).build();
  }
}
