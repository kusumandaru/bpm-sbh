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
  public ExerciseAssessment findById(Integer criteriaScoringId) {
    return repository.findById(criteriaScoringId).get();
  }

  @Override
  public ExerciseAssessment save(ExerciseAssessment criteriaScoring) {
    return repository.save(criteriaScoring);
  }

  @Override
  public Iterable<ExerciseAssessment> saveAll(List<ExerciseAssessment> criteriaScorings) {
    return repository.saveAll(criteriaScorings);
  }

  @Override
  public List<ExerciseAssessment> findByProjectAssessmentID(Integer projectAsessmentId) {
    return repository.findByProjectAssessmentID(projectAsessmentId);
  }
}
