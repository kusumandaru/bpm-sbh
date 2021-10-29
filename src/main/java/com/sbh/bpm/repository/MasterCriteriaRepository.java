package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterCriteria;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterCriteriaRepository extends CrudRepository<MasterCriteria, Integer> {
  List<MasterCriteria> findByMasterExerciseID(Integer exerciseId);
  List<MasterCriteria> findByMasterExerciseIDAndIdNot(Integer exerciseId, Integer blockerId);
}

