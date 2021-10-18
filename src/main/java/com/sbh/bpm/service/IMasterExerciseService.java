package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterExercise;

public interface IMasterExerciseService {

  List<MasterExercise> findAll();
  List<MasterExercise> findByMasterEvaluationID(Integer evaluationId);
  MasterExercise findById(Integer masterExerciseId);
  MasterExercise save(MasterExercise masterExercise);
}
