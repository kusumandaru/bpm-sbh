package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterEvaluation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterEvaluationRepository extends CrudRepository<MasterEvaluation, Integer> {
  List<MasterEvaluation> findByMasterTemplateID(Integer templateId);
}

