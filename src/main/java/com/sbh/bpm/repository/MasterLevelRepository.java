package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterLevel;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterLevelRepository extends CrudRepository<MasterLevel, Integer> {
  MasterLevel findFirstByOrderByMinimumScoreAsc();
  MasterLevel findFirstByMasterTemplateIDOrderByMinimumScoreAsc(Integer templateId);
  List<MasterLevel> findByMasterTemplateID(Integer templateId);
  @Query("select id FROM MasterLevel WHERE masterTemplateID = :templateId")
  List<Integer> getAllIdsByTemplateId(@Param("templateId") Integer templateId);
  @Query("select id FROM MasterLevel WHERE active = TRUE AND masterTemplateID = :templateId")
  List<Integer> getAllIdsByTemplateIdAndActiveTrue(@Param("templateId") Integer templateId);
  List<MasterLevel> findByMasterTemplateIDIn(List<Integer> masterTemplateIds);
}

