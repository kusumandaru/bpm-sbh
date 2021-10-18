package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterEvaluation;

public interface IMasterEvaluationService {

  List<MasterEvaluation> findAll();
  List<MasterEvaluation> findByMasterTemplateID(Integer templateId);
  MasterEvaluation findById(Integer masterEvaluationId);
  MasterEvaluation save(MasterEvaluation masterEvaluation);
}
