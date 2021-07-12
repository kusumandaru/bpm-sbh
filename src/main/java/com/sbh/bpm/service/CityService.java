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
  public List<City> findByProvinceId(Integer provinceId) {
    return (List<City>) repository.findByProvinceId(provinceId);
  }

  @Override
  public City findById(Integer cityId) {
    return repository.findById(cityId).get();
  }

  @Override
  public City save(City city) {
    return repository.save(city);
  }
}
