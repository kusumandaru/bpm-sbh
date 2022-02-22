package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentBuilding;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectDocumentBuildingRepository extends CrudRepository<ProjectDocumentBuilding, Integer> {
  List<ProjectDocumentBuilding> findByMasterTemplateID(Integer masterTemplateID);
  List<ProjectDocumentBuilding> findByMasterTemplateIDAndActiveTrue(Integer masterTemplateID);
  ProjectDocumentBuilding findByMasterTemplateIDAndId(Integer masterTemplateID, Integer documentBuildingId);
}

