package com.sbh.bpm.repository;

import com.sbh.bpm.model.BuildingType;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BuildingTypeRepository extends CrudRepository<BuildingType, Integer> {
  BuildingType findByCode(String code);
}

