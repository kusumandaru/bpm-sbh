package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.City;

public interface ICityService {

  List<City> findAll();
  List<City> findByProvinceId(String provinceId);
  City findyById(String cityId);
}
