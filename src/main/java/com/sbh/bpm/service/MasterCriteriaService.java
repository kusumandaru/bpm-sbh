package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.repository.MasterCriteriaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterCriteriaService implements IMasterCriteriaService {
  @Autowired
  private MasterCriteriaRepository repository;

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
}
