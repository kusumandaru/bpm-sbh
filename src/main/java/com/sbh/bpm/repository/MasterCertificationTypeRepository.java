package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterCertificationType;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterCertificationTypeRepository extends CrudRepository<MasterCertificationType, Integer> {
  List<MasterCertificationType> findByMasterVendorID(Integer vendorId);
  List<MasterCertificationType> findByCertificationCode(String certificationCode);
}

