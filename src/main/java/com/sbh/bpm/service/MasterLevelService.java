package com.sbh.bpm.service;

import java.util.List;

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
}
