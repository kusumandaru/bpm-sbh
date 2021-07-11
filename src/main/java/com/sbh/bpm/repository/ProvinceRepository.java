package com.sbh.bpm.repository;

import com.sbh.bpm.model.Province;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProvinceRepository extends CrudRepository<Province, String> {
}

