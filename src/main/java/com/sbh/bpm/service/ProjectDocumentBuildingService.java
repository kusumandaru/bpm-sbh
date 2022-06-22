package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentBuilding;
import com.sbh.bpm.repository.ProjectDocumentBuildingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectDocumentBuildingService implements IProjectDocumentBuildingService {
  @Autowired
  private ProjectDocumentBuildingRepository repository;

  @Override
  public List<ProjectDocumentBuilding> findAll() {
    return (List<ProjectDocumentBuilding>) repository.findAll();
  }

  @Override
  public ProjectDocumentBuilding findById(Integer projectDocumentBuildingId) {
    return repository.findById(projectDocumentBuildingId).get();
  }

  @Override
  public ProjectDocumentBuilding save(ProjectDocumentBuilding projectDocumentBuilding) {
    return repository.save(projectDocumentBuilding);
  }

  @Override
  public List<ProjectDocumentBuilding> findByMasterCertificationTypeID(Integer masterCertificationTypeID) {
    return repository.findByMasterCertificationTypeID(masterCertificationTypeID);
  }

  @Override
  public ProjectDocumentBuilding findByMasterCertificationTypeIDAndId(Integer masterCertificationTypeID, Integer attachmentId) {
    return repository.findByMasterCertificationTypeIDAndId(masterCertificationTypeID, attachmentId);
  }

  @Override
  public List<ProjectDocumentBuilding> findByMasterCertificationTypeIDAndActiveTrue(Integer masterCertificationTypeID) {
    return repository.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);
  }

  @Override
  public boolean deleteById(Integer attachmentId) {
    repository.deleteById(attachmentId);
    return !repository.existsById(attachmentId);
  }
}
