package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentCategory;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectDocumentCategoryRepository extends CrudRepository<ProjectDocumentCategory, Integer> {
  List<ProjectDocumentCategory> findByActiveTrue();
}

