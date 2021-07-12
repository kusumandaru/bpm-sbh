package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Province;
import com.sbh.bpm.repository.ProvinceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProvinceService implements IProvinceService {
  @Autowired
  private ProvinceRepository repository;

  @Override
  public List<Province> findAll() {
    return (List<Province>) repository.findAll();
  }

  @Override
  public Province findById(Integer provinceId) {
    return repository.findById(provinceId).get();
  }

  @Override
  public Province save(Province province) {
    return repository.save(province);
  }
}
