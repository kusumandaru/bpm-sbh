package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterTemplate;

public interface IMasterTemplateService {

  List<MasterTemplate> findAll();
  List<MasterTemplate> findByMasterVendorID(Integer vendorId);
  MasterTemplate findById(Integer masterTemplateId);
  MasterTemplate save(MasterTemplate masterTemplate);
}
