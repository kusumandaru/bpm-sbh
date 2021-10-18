package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterExercise;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterExerciseRepository extends CrudRepository<MasterExercise, Integer> {
  List<MasterExercise> findByMasterEvaluationID(Integer evaluationId);
}

