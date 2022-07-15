package com.sbh.bpm.service;

import java.util.List;
import java.util.stream.Collectors;

import com.sbh.bpm.model.CriteriaScoring;
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.repository.CriteriaScoringRepository;
import com.sbh.bpm.repository.MasterCriteriaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterCriteriaService implements IMasterCriteriaService {
  @Autowired
  private MasterCriteriaRepository repository;

  @Autowired
  private CriteriaScoringRepository criteriaScoringRepository;

  @Override
  public List<MasterCriteria> findAll() {
    return (List<MasterCriteria>) repository.findAll();
  }
  
  @Override
  public MasterCriteria findById(Integer masterCriteriaID) {
    return repository.findById(masterCriteriaID).get();
  }

  @Override
  public MasterCriteria save(MasterCriteria masterCriteria) {
    return repository.save(masterCriteria);
  }

  @Override
  public List<MasterCriteria> findByMasterExerciseID(Integer exerciseId) {
    return (List<MasterCriteria>) repository.findByMasterExerciseID(exerciseId);
  }

  @Override
  public List<MasterCriteria> withoutSelfSameExercise(Integer criteriaId) {
    MasterCriteria criteria = findById(criteriaId);
    return (List<MasterCriteria>) repository.findByMasterExerciseIDAndIdNot(criteria.getMasterExerciseID(), criteria.getId());
  }

  @Override
  public List<MasterCriteria> findByMasterExerciseIDIn(List<Integer> exerciseIds) {
    return (List<MasterCriteria>) repository.findByMasterExerciseIDIn(exerciseIds);
  }

  @Override
  public List<MasterCriteria> findByMasterExerciseIDInAndActiveTrue(List<Integer> exerciseIds) {
    return (List<MasterCriteria>) repository.findByMasterExerciseIDInAndActiveTrue(exerciseIds);
  }

  @Override
  public List<MasterCriteria> findByProjectAssessmentIDAndSelectedAndPrequisite(Integer projectAsessmentId, boolean selected) {
    List<CriteriaScoring> scorings = criteriaScoringRepository.findByProjectAssessmentIDAndSelected(projectAsessmentId, selected);
    List<Integer> masterCriteriaIds = scorings.stream().map(CriteriaScoring::getMasterCriteriaID).collect(Collectors.toList());
    List<MasterCriteria> criterias = repository.findByExerciseType("prequisite");

    List<MasterCriteria> unselectedCriterias = criterias.stream().filter(criteria -> masterCriteriaIds.contains(criteria.getId())).collect(Collectors.toList());
    return unselectedCriterias;
  }

  @Override
  public boolean deleteById(Integer criteriaId) {
    repository.deleteById(criteriaId);
    return !repository.existsById(criteriaId);
  }
}
