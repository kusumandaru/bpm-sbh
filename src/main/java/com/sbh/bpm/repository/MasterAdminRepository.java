package com.sbh.bpm.repository;

import com.sbh.bpm.model.MasterAdmin;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterAdminRepository extends CrudRepository<MasterAdmin, Integer> {
  MasterAdmin findTopByOrderByIdDesc();
}

