package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterEvaluation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterEvaluationRepository extends CrudRepository<MasterEvaluation, Integer> {
  List<MasterEvaluation> findByMasterTemplateID(Integer templateId);
  @Query("select id FROM MasterEvaluation WHERE masterTemplateID = :templateId")
  List<Integer> getAllIdsByTemplateId(@Param("templateId") Integer templateId);
  List<MasterEvaluation> findByMasterTemplateIDIn(List<Integer> masterTemplateIds);
}

