package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.BuildingType;

public interface IBuildingTypeService {

  List<BuildingType> findAll();
  BuildingType findById(Integer buildingTypeId);
  BuildingType findByCode(String code);
  BuildingType save(BuildingType buildingType);
}
