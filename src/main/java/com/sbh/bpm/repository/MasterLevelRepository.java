package com.sbh.bpm.repository;

import com.sbh.bpm.model.MasterLevel;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterLevelRepository extends CrudRepository<MasterLevel, Integer> {
  MasterLevel findFirstByOrderByMinimumScoreAsc();

  // AndActiveTrue
}

