package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterCriteriaBlocker;

public interface IMasterCriteriaBlockerService {

  List<MasterCriteriaBlocker> findAll();
  List<MasterCriteriaBlocker> findBymasterCriteriaID(Integer criteriaId);
  List<MasterCriteriaBlocker> findBymasterCriteriaIDIn(List<Integer> criteriaIds);
  MasterCriteriaBlocker findById(Integer masterCriteriaBlockerId);
  MasterCriteriaBlocker save(MasterCriteriaBlocker masterCriteriaBlocker);
  void deleteBymasterCriteriaID(Integer criteriaId);
  boolean deleteById(Integer criteriaBlockerId);
}
