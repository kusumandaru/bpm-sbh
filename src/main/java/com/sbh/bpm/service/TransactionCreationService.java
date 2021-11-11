package com.sbh.bpm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sbh.bpm.model.CriteriaScoring;
import com.sbh.bpm.model.DocumentFile;
import com.sbh.bpm.model.ExerciseAssessment;
import com.sbh.bpm.model.MasterAdmin;
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.model.MasterDocument;
import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.model.MasterTemplate;
import com.sbh.bpm.model.ProjectAssessment;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import lombok.Getter;
import lombok.Setter;

@Service
@Transactional
public class TransactionCreationService  implements ITransactionCreationService {

  @Autowired
  private IMasterAdminService masterAdminService;

  @Autowired
  private IMasterTemplateService masterTemplateService;

  @Autowired
  private IMasterEvaluationService masterEvaluationService;

  @Autowired
  private IMasterExerciseService masterExerciseService;

  @Autowired
  private IMasterCriteriaService masterCriteriaService;

  @Autowired
  private IMasterDocumentService masterDocumentService;

  @Autowired
  private IProjectAssessmentService projectAssessmentService;

  @Autowired
  private ICriteriaScoringService criteriaScoringService;

  @Autowired
  private IExerciseAssessmentService exerciseAssessmentService;

  @Autowired
  private IDocumentFileService documentFileService;

  @Autowired
  private PlatformTransactionManager transactionManager;
  
  public class TransactionCreationResponse {
    @Getter
    @Setter
    public final Boolean success;
    
    @Getter
    @Setter
    public final Map<String, String> messages;
  
    public TransactionCreationResponse(Boolean success, Map<String, String> messages) {
      this.success = success;
      this.messages = messages;
    }
  }

  @Override
  public TransactionCreationResponse createDRTransactionForProcessInstance(String processInstanceID) {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    try {
      String activityInstanceId = runtimeService.getActivityInstance(processInstanceID).getId();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "process instance not found");

      return new TransactionCreationResponse(false, map);
    }

    try {
      MasterAdmin masterAdmin = masterAdminService.findLast();
      MasterTemplate masterTemplate = masterTemplateService.findById(masterAdmin.getDrTemplateID());
      Date newDate = new Date();

      ProjectAssessment projectAssessment = new ProjectAssessment();
      projectAssessment.setMasterTemplateID(masterTemplate.getId());
      projectAssessment.setProcessInstanceID(processInstanceID);
      projectAssessment.setProjectName(masterTemplate.getProjectVersion());
      projectAssessment.setPossibleScore(0.0f);
      projectAssessment.setTemporaryScore(0.0f);
      projectAssessment.setPotentialScore(0.0f);
      projectAssessment.setCreatedAt(newDate);
      // change it later
      projectAssessment.setProposedLevelID(1);

      projectAssessment = projectAssessmentService.save(projectAssessment);

      List<Integer> evaluationIds = masterEvaluationService.getAllIdsByTemplateId(masterTemplate.getId());

      List<MasterExercise> masterExerciseList = masterExerciseService.findByMasterEvaluationIDIn(evaluationIds);

      List<ExerciseAssessment> assessments = new ArrayList<ExerciseAssessment>();
      for (MasterExercise exercise : masterExerciseList) {
        ExerciseAssessment assessment = new ExerciseAssessment();
        assessment.setMasterExerciseID(exercise.getId());
        assessment.setProjectAssessmentID(projectAssessment.getId());
        assessment.setSelected(false);
        assessment.setCreatedBy("system");
        assessment.setCreatedAt(newDate);

        assessments.add(assessment);
      }

      exerciseAssessmentService.saveAll(assessments);

      List<Integer> exerciseIds = masterExerciseList.stream().map(MasterExercise::getId).collect(Collectors.toList());

      List<MasterCriteria> masterCriteriaList = masterCriteriaService.findByMasterExerciseIDIn(exerciseIds);

      List<CriteriaScoring> scorings = new ArrayList<CriteriaScoring>();
      for (MasterCriteria criteria : masterCriteriaList) {
        CriteriaScoring scoring = new CriteriaScoring();
        scoring.setMasterCriteriaID(criteria.getId());
        scoring.setProjectAssessmentID(projectAssessment.getId());
        scoring.setSelected(false);
        scoring.setScore(0.0f);
        scoring.setPotentialScore(0.0f);
        scoring.setApprovalStatus(1); // idle
        scoring.setCreatedBy("system");
        scoring.setCreatedAt(newDate);

        scorings.add(scoring);
      }

      Iterable<CriteriaScoring> criteriaScorings = criteriaScoringService.saveAll(scorings);

      List<DocumentFile> docFiles = new ArrayList<DocumentFile>();
      for (CriteriaScoring cs :  scorings) {
        List<MasterDocument> masterDocuments = masterDocumentService.findBymasterCriteriaID(cs.getMasterCriteriaID());
        for (MasterDocument doc : masterDocuments) {
          DocumentFile docFile = new DocumentFile();
          docFile.setMasterDocumentID(doc.getId());
          docFile.setCriteriaScoringID(cs.getId());
          docFile.setCreatedBy("system");
          docFile.setCreatedAt(newDate);

          docFiles.add(docFile);
        }
      }

      Iterable<DocumentFile> documentFiles = documentFileService.saveAll(docFiles);
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      Map<String, String> map = new HashMap<String, String>();
      map.put("message", ex.getMessage());

      return new TransactionCreationResponse(false, map);
  }

    Map<String, String> resultMap = new HashMap<String, String>();
    resultMap.put("message", "DR transaction has been created");
    return new TransactionCreationResponse(true, resultMap);
  }
}
