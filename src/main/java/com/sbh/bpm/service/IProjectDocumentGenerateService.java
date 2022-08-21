package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentGenerate;

public interface IProjectDocumentGenerateService {

  List<ProjectDocumentGenerate> findAll();
  ProjectDocumentGenerate findById(Integer projectDocumentGenerateId);
  ProjectDocumentGenerate save(ProjectDocumentGenerate projectDocumentGenerate);
  List<ProjectDocumentGenerate> findByMasterCertificationTypeID(Integer masterCertificationTypeID);
  List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndProjectDocumentCategoryID(Integer masterCertificationTypeID, Integer categoryID);
  ProjectDocumentGenerate findByMasterCertificationTypeIDAndId(Integer masterCertificationTypeID, Integer documentGenerateId);
  List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndActiveTrue(Integer masterCertificationTypeID);
  List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndProjectDocumentCategoryIDAndActiveTrue(Integer masterCertificationTypeID, Integer categoryID);
  boolean deleteById(Integer attachmentId);
}
