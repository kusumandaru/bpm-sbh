package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterCriteriaBlocker;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterCriteriaBlockerRepository extends CrudRepository<MasterCriteriaBlocker, Integer> {
  List<MasterCriteriaBlocker> findBymasterCriteriaID(Integer criteriaId);
  void deleteBymasterCriteriaID(Integer criteriaId);
}

