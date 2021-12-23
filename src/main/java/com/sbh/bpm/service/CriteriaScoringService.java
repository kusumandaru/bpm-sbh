package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.CriteriaScoring;
import com.sbh.bpm.repository.CriteriaScoringRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CriteriaScoringService implements ICriteriaScoringService {
  @Autowired
  private CriteriaScoringRepository repository;

  @Override
  public List<CriteriaScoring> findAll() {
    return (List<CriteriaScoring>) repository.findAll();
  }

  @Override
  public CriteriaScoring findById(Integer criteriaScoringId) {
    return repository.findById(criteriaScoringId).get();
  }

  @Override
  public CriteriaScoring save(CriteriaScoring criteriaScoring) {
    return repository.save(criteriaScoring);
  }

  @Override
  public Iterable<CriteriaScoring> saveAll(List<CriteriaScoring> criteriaScorings) {
    return repository.saveAll(criteriaScorings);
  }

  @Override
  public List<CriteriaScoring> findByProjectAssessmentID(Integer projectAsessmentId) {
    return repository.findByProjectAssessmentID(projectAsessmentId);
  }

  @Override
  public List<CriteriaScoring> findBySelected(boolean selected) {
    return repository.findBySelected(selected);
  }

  @Override
  public List<CriteriaScoring> findByApprovalStatusIn(List<Integer> approvalStatuses) {
    return repository.findByApprovalStatusIn(approvalStatuses);
  }

  @Override
  public List<CriteriaScoring> findByProjectAssessmentIDAndApprovalStatusIn(Integer projectAssessmentID, List<Integer> approvalStatuses) {
    return repository.findByProjectAssessmentIDAndApprovalStatusIn(projectAssessmentID, approvalStatuses);
  }

  @Override
  public List<CriteriaScoring> findByProjectAssessmentIDAndSelected(Integer projectAsessmentId, boolean selected) {
    return repository.findByProjectAssessmentIDAndSelected(projectAsessmentId, selected);
  }
}
