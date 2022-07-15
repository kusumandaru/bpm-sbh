package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterVendor;
import com.sbh.bpm.repository.MasterVendorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterVendorService implements IMasterVendorService {
  @Autowired
  private MasterVendorRepository repository;

  @Override
  public List<MasterVendor> findAll() {
    return (List<MasterVendor>) repository.findAll();
  }
  
  @Override
  public MasterVendor findById(Integer masterVendorId) {
    return repository.findById(masterVendorId).get();
  }

  @Override
  public MasterVendor save(MasterVendor masterVendor) {
    return repository.save(masterVendor);
  }

  @Override
  public boolean deleteById(Integer vendorId) {
    repository.deleteById(vendorId);
    return !repository.existsById(vendorId);
  }
}
