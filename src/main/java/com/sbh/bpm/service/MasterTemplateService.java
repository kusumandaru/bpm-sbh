package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterTemplate;
import com.sbh.bpm.repository.MasterTemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterTemplateService implements IMasterTemplateService {
  @Autowired
  private MasterTemplateRepository repository;

  @Override
  public List<MasterTemplate> findAll() {
    return (List<MasterTemplate>) repository.findAll();
  }
  
  @Override
  public MasterTemplate findById(Integer masterTemplateId) {
    return repository.findById(masterTemplateId).get();
  }

  @Override
  public MasterTemplate save(MasterTemplate masterTemplate) {
    return repository.save(masterTemplate);
  }

  @Override
  public List<MasterTemplate> findByMasterVendorID(Integer vendorId) {
    return (List<MasterTemplate>) repository.findByMasterVendorID(vendorId);
  }

  @Override
  public List<MasterTemplate> findByMasterCertificationTypeID(Integer masterCertificationTypeId) {
    return (List<MasterTemplate>) repository.findByMasterCertificationTypeID(masterCertificationTypeId);
  }

  @Override
  public List<MasterTemplate> findByProjectType(String projectType) {
    return (List<MasterTemplate>) repository.findByProjectType(projectType);
  }

  @Override
  public boolean deleteById(Integer templateId) {
    repository.deleteById(templateId);
    return !repository.existsById(templateId);
  }

  @Override
  public List<MasterTemplate> findByMasterCertificationTypeIDAndProjectType(Integer certificationTypeId,
      String projectType) {
      return (List<MasterTemplate>) repository.findByMasterCertificationTypeIDAndProjectType(certificationTypeId, projectType);
  }
}
