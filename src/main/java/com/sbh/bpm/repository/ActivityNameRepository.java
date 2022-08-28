package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ActivityName;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ActivityNameRepository extends CrudRepository<ActivityName, Integer> {
  List<ActivityName> findByMasterCertificationTypeIDAndActiveTrue(Integer certificationTypeID);
  List<ActivityName> findByMasterCertificationTypeID(Integer certificationTypeID);
  ActivityName findByMasterCertificationTypeIDAndId(Integer certificationTypeID, Integer documentGenerateId);
  List<ActivityName> findByMasterCertificationTypeIDAndActivityID(Integer masterCertificationTypeID, String activityID);
}

