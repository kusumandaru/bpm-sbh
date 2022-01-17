package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectAssessment;
import com.sbh.bpm.repository.ProjectAssessmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectAssessmentService implements IProjectAssessmentService {
  @Autowired
  private ProjectAssessmentRepository repository;

  @Override
  public List<ProjectAssessment> findAll() {
    return (List<ProjectAssessment>) repository.findAll();
  }

  @Override
  public ProjectAssessment findById(Integer projectAssessmentId) {
    return repository.findById(projectAssessmentId).get();
  }

  @Override
  public ProjectAssessment save(ProjectAssessment projectAssessment) {
    return repository.save(projectAssessment);
  }

  @Override
  public List<ProjectAssessment> findByProcessInstanceID(String processInstanceId) {
    return repository.findByProcessInstanceID(processInstanceId);
  }

  @Override
  public List<ProjectAssessment> findByProcessInstanceIDAndMasterTemplateID(String processInstanceId,
      Integer masterTemplateId) {
    return repository.findByProcessInstanceIDAndMasterTemplateID(processInstanceId, masterTemplateId);
  }

  @Override
  public List<ProjectAssessment> findByProcessInstanceIDAndAssessmentType(String processInstanceId,
      String assessmentType) {
        return repository.findByProcessInstanceIDAndAssessmentType(processInstanceId, assessmentType);

  }
}
