package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.repository.MasterEvaluationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterEvaluationService implements IMasterEvaluationService {
  @Autowired
  private MasterEvaluationRepository repository;

  @Override
  public List<MasterEvaluation> findAll() {
    return (List<MasterEvaluation>) repository.findAll();
  }
  
  @Override
  public MasterEvaluation findById(Integer masterEvaluationId) {
    return repository.findById(masterEvaluationId).get();
  }

  @Override
  public MasterEvaluation save(MasterEvaluation masterEvaluation) {
    return repository.save(masterEvaluation);
  }

  @Override
  public List<MasterEvaluation> findByMasterTemplateID(Integer templateId) {
    return (List<MasterEvaluation>) repository.findByMasterTemplateID(templateId);
  }

  @Override
  public List<Integer> getAllIdsByTemplateId(Integer templateId) {
    return (List<Integer>) repository.getAllIdsByTemplateId(templateId);
  }

  @Override
  public List<MasterEvaluation> findByMasterTemplateIDIn(List<Integer> masterTemplateIds) {
    return (List<MasterEvaluation>) repository.findByMasterTemplateIDIn(masterTemplateIds);

  }
}
