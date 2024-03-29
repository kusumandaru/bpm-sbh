package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterLevel;
import com.sbh.bpm.model.ProjectAssessment;
import com.sbh.bpm.service.TransactionCreationService.TransactionCreationResponse;

public interface ITransactionCreationService {
  TransactionCreationResponse createDRTransactionForProcessInstance(String processInstanceID);
  TransactionCreationResponse createFATransactionForProcessInstance(String processInstanceID);
  TransactionCreationResponse tagSubmittedAttachment(String processInstanceID, Integer masterTemplateID);
  TransactionCreationResponse tagSubmittedAttachmentByAssessmentType(String processInstanceID, String assessmentType);
  ProjectAssessment calculateProjectAssessment(Integer projectAssessmentId);
  MasterLevel getMinimumLevelFromProjectAssessmentID(Integer projectAssessmentID);
  List<MasterLevel> getAllLevelFromProjectAssessmentID(Integer projectAssessmentID);
}
