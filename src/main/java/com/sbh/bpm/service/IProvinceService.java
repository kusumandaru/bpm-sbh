package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Province;

public interface IProvinceService {

  List<Province> findAll();
  Province findyById(String provinceId);
}
