package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.CriteriaScoring;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CriteriaScoringRepository extends CrudRepository<CriteriaScoring, Integer> {
  List<CriteriaScoring> findByProjectAssessmentID(Integer projectAsessmentId);
  List<CriteriaScoring> findBySelected(boolean selected);
  List<CriteriaScoring> findByApprovalStatusIn(List<Integer> approvalStatuses);
  List<CriteriaScoring> findByProjectAssessmentIDAndApprovalStatusIn(Integer projectAssessmentID, List<Integer> approvalStatuses);
  List<CriteriaScoring> findByProjectAssessmentIDAndSelected(Integer projectAsessmentId, boolean selected);
  List<CriteriaScoring> findByExerciseAssessmentIDAndSelected(Integer exerciseAsessmentId, boolean selected);

}

