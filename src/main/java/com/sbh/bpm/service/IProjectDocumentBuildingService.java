package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentBuilding;

public interface IProjectDocumentBuildingService {

  List<ProjectDocumentBuilding> findAll();
  ProjectDocumentBuilding findById(Integer projectDocumentBuildingId);
  ProjectDocumentBuilding save(ProjectDocumentBuilding projectDocumentBuilding);
  List<ProjectDocumentBuilding> findByMasterCertificationTypeID(Integer masterCertificationTypeID);
  List<ProjectDocumentBuilding> findByMasterCertificationTypeIDAndProjectDocumentCategoryID(Integer masterCertificationTypeID, Integer categoryID);
  ProjectDocumentBuilding findByMasterCertificationTypeIDAndId(Integer masterCertificationTypeID, Integer documentBuildingId);
  List<ProjectDocumentBuilding> findByMasterCertificationTypeIDAndActiveTrue(Integer masterCertificationTypeID);
  List<ProjectDocumentBuilding> findByMasterCertificationTypeIDAndProjectDocumentCategoryIDAndActiveTrue(Integer masterCertificationTypeID, Integer categoryID);
  boolean deleteById(Integer attachmentId);
}
