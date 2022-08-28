package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ActivityName;
import com.sbh.bpm.repository.ActivityNameRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityNameService implements IActivityNameService {
  @Autowired
  private ActivityNameRepository repository;

  @Override
  public List<ActivityName> findAll() {
    return (List<ActivityName>) repository.findAll();
  }

  @Override
  public ActivityName findById(Integer projectDocumentGenerateId) {
    return repository.findById(projectDocumentGenerateId).get();
  }

  @Override
  public ActivityName save(ActivityName projectDocumentGenerate) {
    return repository.save(projectDocumentGenerate);
  }

  @Override
  public List<ActivityName> findByMasterCertificationTypeID(Integer masterCertificationTypeID) {
    return repository.findByMasterCertificationTypeID(masterCertificationTypeID);
  }

  @Override
  public ActivityName findByMasterCertificationTypeIDAndId(Integer masterCertificationTypeID, Integer attachmentId) {
    return repository.findByMasterCertificationTypeIDAndId(masterCertificationTypeID, attachmentId);
  }

  @Override
  public List<ActivityName> findByMasterCertificationTypeIDAndActiveTrue(Integer masterCertificationTypeID) {
    return repository.findByMasterCertificationTypeIDAndActiveTrue(masterCertificationTypeID);
  }

  @Override
  public boolean deleteById(Integer attachmentId) {
    repository.deleteById(attachmentId);
    return !repository.existsById(attachmentId);
  }

  @Override
  public List<ActivityName> findByMasterCertificationTypeIDAndActivityID(Integer masterCertificationTypeID, String activityID) {
      return repository.findByMasterCertificationTypeIDAndActivityID(masterCertificationTypeID, activityID);
   
  }
}
