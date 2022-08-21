package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentGenerate;
import com.sbh.bpm.repository.ProjectDocumentGenerateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectDocumentGenerateService implements IProjectDocumentGenerateService {
  @Autowired
  private ProjectDocumentGenerateRepository repository;

  @Override
  public List<ProjectDocumentGenerate> findAll() {
    return (List<ProjectDocumentGenerate>) repository.findAll();
  }

  @Override
  public ProjectDocumentGenerate findById(Integer projectDocumentGenerateId) {
    return repository.findById(projectDocumentGenerateId).get();
  }

  @Override
  public ProjectDocumentGenerate save(ProjectDocumentGenerate projectDocumentGenerate) {
    return repository.save(projectDocumentGenerate);
  }

  @Override
  public List<ProjectDocumentGenerate> findByMasterCertificationTypeID(Integer masterCertificationTypeID) {
    return repository.findByMasterCertificationTypeID(masterCertificationTypeID);
  }

  @Override
  public List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndProjectDocumentCategoryID(Integer masterCertificationTypeID,
      Integer categoryID) {
        return repository.findByMasterCertificationTypeIDAndProjectDocumentCategoryID(masterCertificationTypeID, categoryID);
  }

  @Override
  public ProjectDocumentGenerate findByMasterCertificationTypeIDAndId(Integer masterCertificationTypeID, Integer attachmentId) {
    return repository.findByMasterCertificationTypeIDAndId(masterCertificationTypeID, attachmentId);
  }

  @Override
  public List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndActiveTrue(Integer masterCertificationTypeID) {
    return repository.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);
  }

  @Override
  public List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndProjectDocumentCategoryIDAndActiveTrue(
      Integer masterCertificationTypeID, Integer categoryID) {
    return repository.findByMasterCertificationTypeIDAndProjectDocumentCategoryIDAndActiveTrue(masterCertificationTypeID, categoryID);
  }

  @Override
  public boolean deleteById(Integer attachmentId) {
    repository.deleteById(attachmentId);
    return !repository.existsById(attachmentId);
  }
}
