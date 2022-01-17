package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectAssessment;

public interface IProjectAssessmentService {

  List<ProjectAssessment> findAll();
  ProjectAssessment findById(Integer projectAssessmentId);
  ProjectAssessment save(ProjectAssessment projectAssessment);
  List<ProjectAssessment> findByProcessInstanceID(String processInstanceId);
  List<ProjectAssessment> findByProcessInstanceIDAndMasterTemplateID(String processInstanceId, Integer masterTemplateID);
  List<ProjectAssessment> findByProcessInstanceIDAndAssessmentType(String processInstanceId, String assessmentType);

}
