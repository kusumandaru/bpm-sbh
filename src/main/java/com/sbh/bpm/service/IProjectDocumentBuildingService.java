package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentBuilding;

public interface IProjectDocumentBuildingService {

  List<ProjectDocumentBuilding> findAll();
  ProjectDocumentBuilding findById(Integer projectDocumentBuildingId);
  ProjectDocumentBuilding save(ProjectDocumentBuilding projectDocumentBuilding);
  List<ProjectDocumentBuilding> findByMasterTemplateID(Integer masterTemplateID);
  ProjectDocumentBuilding findByMasterTemplateIDAndId(Integer masterTemplateID, Integer documentBuildingId);
  List<ProjectDocumentBuilding> findByMasterTemplateIDAndActiveTrue(Integer masterTemplateID);
  boolean deleteById(Integer attachmentId);
}
