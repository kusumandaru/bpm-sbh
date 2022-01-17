package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ProjectAssessment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectAssessmentRepository extends CrudRepository<ProjectAssessment, Integer> {
  List<ProjectAssessment> findByProcessInstanceID(String processInstanceId);
  List<ProjectAssessment> findByProcessInstanceIDAndMasterTemplateID(String processInstanceId, Integer masterTemplateId);
  List<ProjectAssessment> findByProcessInstanceIDAndAssessmentType(String processInstanceId, String assessmentType);
}

