package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterScoreModifier;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterScoreModifierRepository extends CrudRepository<MasterScoreModifier, Integer> {
  List<MasterScoreModifier> findByMasterExerciseID(Integer exerciseId);
  List<MasterScoreModifier> findByMasterExerciseIDIn(List<Integer> exerciseIds);
  List<MasterScoreModifier> findByMasterExerciseIDInAndActiveTrue(List<Integer> exerciseIds);
}

