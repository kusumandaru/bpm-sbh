package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ExerciseAssessment;
import com.sbh.bpm.repository.ExerciseAssessmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExerciseAssessmentService implements IExerciseAssessmentService {
  @Autowired
  private ExerciseAssessmentRepository repository;

  @Override
  public List<ExerciseAssessment> findAll() {
    return (List<ExerciseAssessment>) repository.findAll();
  }

  @Override
  public ExerciseAssessment findById(Integer exerciseAssessmentId) {
    return repository.findById(exerciseAssessmentId).get();
  }

  @Override
  public ExerciseAssessment save(ExerciseAssessment exerciseAssessment) {
    return repository.save(exerciseAssessment);
  }

  @Override
  public Iterable<ExerciseAssessment> saveAll(List<ExerciseAssessment> exerciseAssessments) {
    return repository.saveAll(exerciseAssessments);
  }

  @Override
  public List<ExerciseAssessment> findByProjectAssessmentID(Integer projectAsessmentId) {
    return repository.findByProjectAssessmentID(projectAsessmentId);
  }
}
