package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentGenerate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectDocumentGenerateRepository extends CrudRepository<ProjectDocumentGenerate, Integer> {
  List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndActiveTrue(Integer certificationTypeID);
  List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndProjectDocumentCategoryIDAndActiveTrue(Integer certificationTypeID, Integer categoryID);
  List<ProjectDocumentGenerate> findByMasterCertificationTypeID(Integer certificationTypeID);
  List<ProjectDocumentGenerate> findByMasterCertificationTypeIDAndProjectDocumentCategoryID(Integer certificationTypeID, Integer categoryID);
  ProjectDocumentGenerate findByMasterCertificationTypeIDAndId(Integer certificationTypeID, Integer documentGenerateId);
}

