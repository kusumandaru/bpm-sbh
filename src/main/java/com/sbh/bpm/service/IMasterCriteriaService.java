package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterCriteria;

public interface IMasterCriteriaService {

  List<MasterCriteria> findAll();
  List<MasterCriteria> findByMasterExerciseID(Integer exerciseId);
  List<MasterCriteria> findByMasterExerciseIDIn(List<Integer> exerciseIds);
  List<MasterCriteria> findByMasterExerciseIDInAndActiveTrue(List<Integer> exerciseIds);
  List<MasterCriteria> withoutSelfSameExercise(Integer criteriaId);
  MasterCriteria findById(Integer masterCriteriaID);
  MasterCriteria save(MasterCriteria masterCriteria);
  List<MasterCriteria> findByProjectAssessmentIDAndSelectedAndPrequisite(Integer projectAsessmentId, boolean selected);
  boolean deleteById(Integer criteriaId);
}
