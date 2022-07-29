package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterTemplate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterTemplateRepository extends CrudRepository<MasterTemplate, Integer> {
  List<MasterTemplate> findByMasterVendorID(Integer vendorId);
  List<MasterTemplate> findByMasterCertificationTypeID(Integer certificationTypeId);
  List<MasterTemplate> findByProjectType(String projectTypes);
  List<MasterTemplate> findByMasterCertificationTypeIDAndProjectType(Integer certificationTypeId, String projectType);
}

