package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ActivityName;

public interface IActivityNameService {

  List<ActivityName> findAll();
  ActivityName findById(Integer projectDocumentGenerateId);
  ActivityName save(ActivityName projectDocumentGenerate);
  List<ActivityName> findByMasterCertificationTypeID(Integer masterCertificationTypeID);
  List<ActivityName> findByMasterCertificationTypeIDAndActivityID(Integer masterCertificationTypeID, String activityID);
  ActivityName findByMasterCertificationTypeIDAndId(Integer masterCertificationTypeID, Integer documentGenerateId);
  List<ActivityName> findByMasterCertificationTypeIDAndActiveTrue(Integer masterCertificationTypeID);
  boolean deleteById(Integer attachmentId);
}
