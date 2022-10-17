package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentBuilding;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectDocumentBuildingRepository extends CrudRepository<ProjectDocumentBuilding, Integer> {
  List<ProjectDocumentBuilding> findByMasterCertificationTypeIDAndActiveTrue(Integer certificationTypeID);
  List<ProjectDocumentBuilding> findByMasterCertificationTypeIDAndProjectDocumentCategoryIDAndActiveTrue(Integer certificationTypeID, Integer categoryID);
  List<ProjectDocumentBuilding> findByMasterCertificationTypeID(Integer certificationTypeID);
  List<ProjectDocumentBuilding> findByMasterCertificationTypeIDAndProjectDocumentCategoryID(Integer certificationTypeID, Integer categoryID);
  ProjectDocumentBuilding findByMasterCertificationTypeIDAndId(Integer certificationTypeID, Integer documentBuildingId);
}

