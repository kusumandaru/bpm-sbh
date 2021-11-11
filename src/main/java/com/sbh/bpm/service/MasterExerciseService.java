package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.repository.MasterExerciseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterExerciseService implements IMasterExerciseService {
  @Autowired
  private MasterExerciseRepository repository;

  @Override
  public List<MasterExercise> findAll() {
    return (List<MasterExercise>) repository.findAll();
  }
  
  @Override
  public MasterExercise findById(Integer masterExerciseId) {
    return repository.findById(masterExerciseId).get();
  }

  @Override
  public MasterExercise save(MasterExercise masterExercise) {
    return repository.save(masterExercise);
  }

  @Override
  public List<MasterExercise> findByMasterEvaluationID(Integer evaluationId) {
    return (List<MasterExercise>) repository.findByMasterEvaluationID(evaluationId);
  }

  @Override
  public List<MasterExercise> findByMasterEvaluationIDIn(List<Integer> evaluationIds) {
    return (List<MasterExercise>) repository.findByMasterEvaluationIDIn(evaluationIds);
  }
}
