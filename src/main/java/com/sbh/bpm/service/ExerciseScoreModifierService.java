package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ExerciseScoreModifier;
import com.sbh.bpm.repository.ExerciseScoreModifierRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExerciseScoreModifierService implements IExerciseScoreModifierService {
  @Autowired
  private ExerciseScoreModifierRepository repository;

  @Override
  public List<ExerciseScoreModifier> findAll() {
    return (List<ExerciseScoreModifier>) repository.findAll();
  }
  
  @Override
  public ExerciseScoreModifier findById(Integer id) {
    return repository.findById(id).get();
  }

  @Override
  public ExerciseScoreModifier save(ExerciseScoreModifier scoreModifier) {
    return repository.save(scoreModifier);
  }

  @Override
  public Iterable<ExerciseScoreModifier> saveAll(List<ExerciseScoreModifier> scoreModifiers) {
    return repository.saveAll(scoreModifiers);
  }

  @Override
  public List<ExerciseScoreModifier> findByMasterScoreModifierID(Integer modifierID) {
    return (List<ExerciseScoreModifier>) repository.findByMasterScoreModifierID(modifierID);
  }

  @Override
  public List<ExerciseScoreModifier> findByMasterScoreModifierIDIn(List<Integer> modifierID) {
    return (List<ExerciseScoreModifier>) repository.findByMasterScoreModifierIDIn(modifierID);
  }

  @Override
  public void deleteByMasterScoreModifierID(Integer criteriaId) {
    repository.deleteByMasterScoreModifierID(criteriaId);
  }

  @Override
  public boolean deleteById(Integer criteriaBlockerId) {
    repository.deleteById(criteriaBlockerId);
    return !repository.existsById(criteriaBlockerId);
  }

  @Override
  public List<ExerciseScoreModifier> findByProjectAssessmentID(Integer projectAssessmentId) {
    return repository.findByProjectAssessmentID(projectAssessmentId);
  }
}
