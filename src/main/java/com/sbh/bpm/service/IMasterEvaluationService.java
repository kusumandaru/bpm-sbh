package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterEvaluation;

public interface IMasterEvaluationService {

  List<MasterEvaluation> findAll();
  List<MasterEvaluation> findByMasterTemplateID(Integer templateId);
  List<Integer> getAllIdsByTemplateId(Integer templateId);
  List<Integer> getAllIdsByTemplateIdAndActiveTrue(Integer templateId);
  MasterEvaluation findById(Integer masterEvaluationId);
  MasterEvaluation save(MasterEvaluation masterEvaluation);
  List<MasterEvaluation> findByMasterTemplateIDIn(List<Integer> masterTemplateIds);
}
