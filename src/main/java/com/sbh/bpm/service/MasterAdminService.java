package com.sbh.bpm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  public MasterAdmin findById(Integer masterAdminId) {
    return repository.findById(masterAdminId).get();
  }

  @Override
  public MasterAdmin save(MasterAdmin masterAdmin) {
    return repository.save(masterAdmin);
  }

  @Override
  public MasterAdmin findLast(){
    return repository.findTopByOrderByIdDesc();
  }

  @Override
  public Map<String, Object> getVariableMap() {
    MasterAdmin masterAdmin = findLast();
    Map<String, Object> variableMap = new HashMap<String, Object>();
    variableMap.put("manager_signature", masterAdmin.getManagerSignature());
    variableMap.put("registration_letter", masterAdmin.getRegistrationLetter());
    variableMap.put("first_attachment", masterAdmin.getFirstAttachment());
    variableMap.put("second_attachment", masterAdmin.getSecondAttachment());
    variableMap.put("third_attachment", masterAdmin.getThirdAttachment());

    return variableMap;
  }
}
