package com.sbh.bpm.controller;

import java.util.Date;
import java.util.List;

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
import com.sbh.bpm.service.IProjectDocumentBuildingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class DocumentBuildingController extends GcsUtil {
  private static final Logger logger = LoggerFactory.getLogger(DocumentBuildingController.class);

  @Autowired
  private IProjectDocumentBuildingService projectDocumentBuildingService;

  @GET
  @Path(value = "/project/document_buildings")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetAllDocumentBuilding(@HeaderParam("Authorization") String authorization) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findAll();

    String json = new Gson().toJson(documentBuildings);
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
  @Path(value = "/project/active_document_buildings/{master_template_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentBuilding(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_template_id") Integer masterTemplateID
  ) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findByMasterTemplateIDAndActiveTrue(masterTemplateID);

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/document_buildings/{master_template_id}/master_template")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDocumentBuildingByMasterTemplate(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_template_id") Integer masterTemplateID
  ) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findByMasterTemplateID(masterTemplateID);

    String json = new Gson().toJson(documentBuildings);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/active_document_buildings/{master_template_id}/master_template")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetActiveDocumentBuildingByMasterTemplate(@HeaderParam("Authorization") String authorization, 
    @PathParam("master_template_id") Integer masterTemplateID
  ) {
    List<ProjectDocumentBuilding> documentBuildings = projectDocumentBuildingService.findByMasterTemplateIDAndActiveTrue(masterTemplateID);

    String json = new Gson().toJson(documentBuildings);
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
    building = projectDocumentBuildingService.save(building);

    String json = new Gson().toJson(building);
    return Response.status(200).entity(json).build();
  }

  @PATCH
  @Path(value = "/project/document_buildings/{document_building_id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateProvinces(@HeaderParam("Authorization") String authorization,
                               @PathParam("document_building_id") Integer documentBuildingId, 
                               ProjectDocumentBuilding building) {   
    ProjectDocumentBuilding pdb = (ProjectDocumentBuilding) projectDocumentBuildingService.findById(documentBuildingId);
    if (pdb == null) {
      return Response.status(400, "project document building not found").build();
    }

    pdb.setName(building.getName());
    pdb.setCode(building.getCode());
    pdb.setPlaceholder(building.getPlaceholder());
    pdb.setObjectType(building.getObjectType());
    pdb.setMandatory(building.getMandatory());
    pdb.setActive(building.getActive());
    pdb.setCreatedBy(building.getCreatedBy());

    pdb = projectDocumentBuildingService.save(pdb);

    String json = new Gson().toJson(pdb);
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
}
