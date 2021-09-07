package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterAdmin;
import com.sbh.bpm.repository.MasterAdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterAdminService implements IMasterAdminService {
  @Autowired
  private MasterAdminRepository repository;

  @Override
  public List<MasterAdmin> findAll() {
    return (List<MasterAdmin>) repository.findAll();
  }
  
  @Override
  public MasterAdmin findById(Integer buildingTypeId) {
    return repository.findById(buildingTypeId).get();
  }

  @Override
  public MasterAdmin save(MasterAdmin buildingType) {
    return repository.save(buildingType);
  }
}
