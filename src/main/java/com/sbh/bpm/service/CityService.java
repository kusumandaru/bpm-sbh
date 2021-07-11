package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.City;
import com.sbh.bpm.repository.CityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityService implements ICityService {
  @Autowired
  private CityRepository repository;

  @Override
  public List<City> findAll() {
    return (List<City>) repository.findAll();
  }

  @Override
  public List<City> findByProvinceId(String provinceId) {
    return (List<City>) repository.findByProvinceId(provinceId);
  }

  @Override
  public City findyById(String cityId) {
    return repository.findById(cityId).get();
  }
}
