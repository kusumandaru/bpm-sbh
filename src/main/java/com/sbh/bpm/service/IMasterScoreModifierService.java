package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterScoreModifier;

public interface IMasterScoreModifierService {

  List<MasterScoreModifier> findAll();
  List<MasterScoreModifier> findByMasterExerciseID(Integer exerciseId);
  List<MasterScoreModifier> findByMasterExerciseIDIn(List<Integer> exerciseIds);
  List<MasterScoreModifier> findByMasterExerciseIDInAndActiveTrue(List<Integer> exerciseIds);
  MasterScoreModifier findById(Integer masterScoreModifierID);
  MasterScoreModifier save(MasterScoreModifier masterScoreModifier);
  boolean deleteById(Integer criteriaId);
}
