package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.City;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CityRepository extends CrudRepository<City, Integer> {
  List<City> findByProvinceId(Integer provinceId);
}

