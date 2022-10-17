package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterScoreModifier;
import com.sbh.bpm.repository.MasterScoreModifierRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterScoreModifierService implements IMasterScoreModifierService {
  @Autowired
  private MasterScoreModifierRepository repository;

  @Override
  public List<MasterScoreModifier> findAll() {
    return (List<MasterScoreModifier>) repository.findAll();
  }
  
  @Override
  public MasterScoreModifier findById(Integer masterScoreModifierID) {
    return repository.findById(masterScoreModifierID).get();
  }

  @Override
  public MasterScoreModifier save(MasterScoreModifier masterScoreModifier) {
    return repository.save(masterScoreModifier);
  }

  @Override
  public List<MasterScoreModifier> findByMasterExerciseID(Integer exerciseId) {
    return (List<MasterScoreModifier>) repository.findByMasterExerciseID(exerciseId);
  }

  @Override
  public List<MasterScoreModifier> findByMasterExerciseIDIn(List<Integer> exerciseIds) {
    return (List<MasterScoreModifier>) repository.findByMasterExerciseIDIn(exerciseIds);
  }

  @Override
  public List<MasterScoreModifier> findByMasterExerciseIDInAndActiveTrue(List<Integer> exerciseIds) {
    return (List<MasterScoreModifier>) repository.findByMasterExerciseIDInAndActiveTrue(exerciseIds);
  }

  @Override
  public boolean deleteById(Integer criteriaId) {
    repository.deleteById(criteriaId);
    return !repository.existsById(criteriaId);
  }
}
