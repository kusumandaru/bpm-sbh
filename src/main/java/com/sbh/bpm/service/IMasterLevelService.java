package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterLevel;

public interface IMasterLevelService {

  List<MasterLevel> findAll();
  List<MasterLevel> findByMasterTemplateID(Integer templateId);
  List<MasterLevel> findByMasterTemplateIDIn(List<Integer> masterTemplateIds);
  List<Integer> getAllIdsByTemplateId(Integer templateId);
  List<Integer> getAllIdsByTemplateIdAndActiveTrue(Integer templateId);
  MasterLevel findById(Integer masterLevelId);
  MasterLevel save(MasterLevel masterLevel);
  MasterLevel findFirstByOrderByMinimumScoreAsc();
  MasterLevel findFirstByMasterTemplateIDOrderByMinimumScoreAsc(Integer templateId);
  boolean deleteById(Integer levelId);
  MasterLevel getLevelByScoreAndTemplateId(Float score, Integer templateId);
}
