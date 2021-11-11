package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ExerciseAssessment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExerciseAssessmentRepository extends CrudRepository<ExerciseAssessment, Integer> {
  List<ExerciseAssessment> findByProjectAssessmentID(Integer projectAsessmentId);
}

