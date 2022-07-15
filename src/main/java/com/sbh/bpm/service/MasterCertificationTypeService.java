package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterCertificationType;
import com.sbh.bpm.repository.MasterCertificationTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterCertificationTypeService implements IMasterCertificationTypeService {
  @Autowired
  private MasterCertificationTypeRepository repository;

  @Override
  public List<MasterCertificationType> findAll() {
    return (List<MasterCertificationType>) repository.findAll();
  }
  
  @Override
  public MasterCertificationType findById(Integer masterCertificationTypeId) {
    return repository.findById(masterCertificationTypeId).get();
  }

  @Override
  public MasterCertificationType save(MasterCertificationType masterCertificationType) {
    return repository.save(masterCertificationType);
  }

  @Override
  public List<MasterCertificationType> findByMasterVendorID(Integer vendorId) {
    return (List<MasterCertificationType>) repository.findByMasterVendorID(vendorId);
  }

  @Override
  public List<MasterCertificationType> findByCertificationCode(String certificationCode) {
    return (List<MasterCertificationType>) repository.findByCertificationCode(certificationCode);
  }

  @Override
  public boolean deleteById(Integer certificationTypeId) {
    repository.deleteById(certificationTypeId);
    return !repository.existsById(certificationTypeId);
  }
}
