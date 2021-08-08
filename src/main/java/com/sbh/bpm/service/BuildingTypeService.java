package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.repository.BuildingTypeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildingTypeService implements IBuildingTypeService {
  @Autowired
  private BuildingTypeRepository repository;

  @Override
  public List<BuildingType> findAll() {
    return (List<BuildingType>) repository.findAll();
  }
  
  @Override
  public BuildingType findById(Integer buildingTypeId) {
    return repository.findById(buildingTypeId).get();
  }

  @Override
  public BuildingType save(BuildingType buildingType) {
    return repository.save(buildingType);
  }
}
