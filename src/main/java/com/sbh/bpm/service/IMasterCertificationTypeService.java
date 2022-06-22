package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterCertificationType;

public interface IMasterCertificationTypeService {

  List<MasterCertificationType> findAll();
  List<MasterCertificationType> findByMasterVendorID(Integer vendorId);
  List<MasterCertificationType> findByCertificationCode(String certificationCode);
  MasterCertificationType findById(Integer masterCertificationTypeId);
  MasterCertificationType save(MasterCertificationType masterCertificationType);
}
