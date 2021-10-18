package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterCriteriaBlocker;
import com.sbh.bpm.repository.MasterCriteriaBlockerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterCriteriaBlockerService implements IMasterCriteriaBlockerService {
  @Autowired
  private MasterCriteriaBlockerRepository repository;

  @Override
  public List<MasterCriteriaBlocker> findAll() {
    return (List<MasterCriteriaBlocker>) repository.findAll();
  }
  
  @Override
  public MasterCriteriaBlocker findById(Integer masterCriteriaBlockerId) {
    return repository.findById(masterCriteriaBlockerId).get();
  }

  @Override
  public MasterCriteriaBlocker save(MasterCriteriaBlocker masterCriteriaBlocker) {
    return repository.save(masterCriteriaBlocker);
  }

  @Override
  public List<MasterCriteriaBlocker> findBymasterCriteriaID(Integer criteriaId) {
    return (List<MasterCriteriaBlocker>) repository.findBymasterCriteriaID(criteriaId);
  }
}
