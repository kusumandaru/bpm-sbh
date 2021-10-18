package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterVendor;

public interface IMasterVendorService {

  List<MasterVendor> findAll();
  MasterVendor findById(Integer masterVendorId);
  MasterVendor save(MasterVendor masterVendor);
}
