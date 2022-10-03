package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ExerciseScoreModifier;

public interface IExerciseScoreModifierService {

  List<ExerciseScoreModifier> findAll();
  List<ExerciseScoreModifier> findByMasterScoreModifierID(Integer modifierId);
  List<ExerciseScoreModifier> findByMasterScoreModifierIDIn(List<Integer> modifierIds);
  ExerciseScoreModifier findById(Integer id);
  ExerciseScoreModifier save(ExerciseScoreModifier scoreModifier);
  void deleteByMasterScoreModifierID(Integer modifierId);
  boolean deleteById(Integer id);
  Iterable<ExerciseScoreModifier> saveAll(List<ExerciseScoreModifier> modifiers);
  List<ExerciseScoreModifier> findByProjectAssessmentID(Integer projectAssessmentId);
  List<ExerciseScoreModifier> findByProjectAssessmentIDAndEnabled(Integer projectAssessmentId, boolean enabled);
}
