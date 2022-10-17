package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectDocumentCategory;
import com.sbh.bpm.repository.ProjectDocumentCategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectDocumentCategoryService implements IProjectDocumentCategoryService {
  @Autowired
  private ProjectDocumentCategoryRepository repository;

  @Override
  public List<ProjectDocumentCategory> findAll() {
    return (List<ProjectDocumentCategory>) repository.findAll();
  }

  @Override
  public ProjectDocumentCategory findById(Integer projectDocumentCategoryID) {
    return repository.findById(projectDocumentCategoryID).get();
  }

  @Override
  public ProjectDocumentCategory save(ProjectDocumentCategory ProjectDocumentCategory) {
    return repository.save(ProjectDocumentCategory);
  }

  @Override
  public boolean deleteById(Integer categoryID) {
    repository.deleteById(categoryID);
    return !repository.existsById(categoryID);
  }


}
