package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ExerciseAssessment;

public interface IExerciseAssessmentService {

  List<ExerciseAssessment> findAll();
  ExerciseAssessment findById(Integer criteriaScoringId);
  ExerciseAssessment save(ExerciseAssessment criteriaScoring);
  Iterable<ExerciseAssessment> saveAll(List<ExerciseAssessment> criteriaScorings);
  List<ExerciseAssessment> findByProjectAssessmentID(Integer projectAsessmentId);
}
