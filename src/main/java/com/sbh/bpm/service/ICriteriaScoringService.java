package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.CriteriaScoring;

public interface ICriteriaScoringService {

  List<CriteriaScoring> findAll();
  CriteriaScoring findById(Integer criteriaScoringId);
  CriteriaScoring save(CriteriaScoring criteriaScoring);
  Iterable<CriteriaScoring> saveAll(List<CriteriaScoring> criteriaScorings);
  List<CriteriaScoring> findByProjectAssessmentID(Integer projectAsessmentId);
}
