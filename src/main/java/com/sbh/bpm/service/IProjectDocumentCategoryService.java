package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentCategory;

public interface IProjectDocumentCategoryService {

  List<ProjectDocumentCategory> findAll();
  ProjectDocumentCategory findById(Integer projectDocumentCategoryID);
  ProjectDocumentCategory save(ProjectDocumentCategory ProjectDocumentCategory);
  boolean deleteById(Integer categoryID);
}
