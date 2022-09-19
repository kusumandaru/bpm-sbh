package com.sbh.bpm.service;

import java.util.List;
import java.util.Objects;

import com.sbh.bpm.model.MasterLevel;
import com.sbh.bpm.repository.MasterLevelRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterLevelService implements IMasterLevelService {
  @Autowired
  private MasterLevelRepository repository;

  @Override
  public List<MasterLevel> findAll() {
    return (List<MasterLevel>) repository.findAll();
  }
  
  @Override
  public MasterLevel findById(Integer masterLevelId) {
    return repository.findById(masterLevelId).get();
  }

  @Override
  public MasterLevel save(MasterLevel masterLevel) {
    return repository.save(masterLevel);
  }

  @Override
  public MasterLevel findFirstByOrderByMinimumScoreAsc() {
    return repository.findFirstByOrderByMinimumScoreAsc();
  }

  @Override
  public MasterLevel findFirstByMasterTemplateIDOrderByMinimumScoreAsc(Integer templateId) {
    return repository.findFirstByMasterTemplateIDOrderByMinimumScoreAsc(templateId);
  }

  @Override
  public List<MasterLevel> findByMasterTemplateID(Integer templateId) {
    return (List<MasterLevel>) repository.findByMasterTemplateID(templateId);
  }

  @Override
  public List<Integer> getAllIdsByTemplateId(Integer templateId) {
    return (List<Integer>) repository.getAllIdsByTemplateId(templateId);
  }

  @Override
  public List<Integer> getAllIdsByTemplateIdAndActiveTrue(Integer templateId) {
    return (List<Integer>) repository.getAllIdsByTemplateIdAndActiveTrue(templateId);
  }

  @Override
  public List<MasterLevel> findByMasterTemplateIDIn(List<Integer> masterTemplateIds) {
    return (List<MasterLevel>) repository.findByMasterTemplateIDIn(masterTemplateIds);

  }

  @Override
  public boolean deleteById(Integer levelId) {
    repository.deleteById(levelId);
    return !repository.existsById(levelId);
  }

  @Override
  public MasterLevel getLevelByScoreAndTemplateId(Float score, Integer templateId) {
    MasterLevel level = repository.getLevelByScoreAndTemplateId(score, templateId);
    if (Objects.isNull(level)) {
      level = repository.findFirstByMasterTemplateIDOrderByMinimumScoreAsc(templateId);
    }
    return level;
  }
}
