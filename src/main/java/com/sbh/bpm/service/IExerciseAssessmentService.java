package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ExerciseAssessment;

public interface IExerciseAssessmentService {

  List<ExerciseAssessment> findAll();
  ExerciseAssessment findById(Integer exerciseAssessmentId);
  ExerciseAssessment save(ExerciseAssessment exerciseAssessment);
  Iterable<ExerciseAssessment> saveAll(List<ExerciseAssessment> exerciseAssessments);
  List<ExerciseAssessment> findByProjectAssessmentID(Integer projectAsessmentId);
}
