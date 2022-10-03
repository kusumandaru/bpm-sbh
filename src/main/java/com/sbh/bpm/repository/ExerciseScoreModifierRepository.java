package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ExerciseScoreModifier;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExerciseScoreModifierRepository extends CrudRepository<ExerciseScoreModifier, Integer> {
  List<ExerciseScoreModifier> findByMasterScoreModifierID(Integer modifierId);
  List<ExerciseScoreModifier> findByMasterScoreModifierIDIn(List<Integer> modifierIds);
  void deleteByMasterScoreModifierID(Integer modifierId);
  List<ExerciseScoreModifier> findByProjectAssessmentID(Integer projectAssessmentId);
  List<ExerciseScoreModifier> findByProjectAssessmentIDAndEnabled(Integer projectAssessmentId, boolean enabled);
}

