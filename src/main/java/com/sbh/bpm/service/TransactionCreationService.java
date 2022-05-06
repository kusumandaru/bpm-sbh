package com.sbh.bpm.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.sbh.bpm.model.Attachment;
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
public class TransactionCreationService implements ITransactionCreationService {

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
  private IAttachmentService attachmentService;

  @Autowired
  private PlatformTransactionManager transactionManager;
  
  public class TransactionCreationResponse {
    @Getter
    @Setter
    public Boolean success;
    
    @Getter
    @Setter
    public Map<String, String> messages;
  
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
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "process instance not found");

      return new TransactionCreationResponse(false, map);
    }
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, "DR");
    if (projectAssessments.size() > 0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "DR Transaction already created");

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
      projectAssessment.setPotentialScore(0.0f);
      projectAssessment.setApprovedScore(0.0f);
      projectAssessment.setSubmittedScore(0.0f);
      projectAssessment.setScoreModifier(0.0f);
      projectAssessment.setCreatedAt(newDate);
      projectAssessment.setAssessmentType("DR");
      projectAssessment.setProposedLevelID(masterAdmin.getDefaultDRLevel());

      projectAssessment = projectAssessmentService.save(projectAssessment);

      List<Integer> evaluationIds = masterEvaluationService.getAllIdsByTemplateIdAndActiveTrue(masterTemplate.getId());

      List<MasterExercise> masterExerciseList = masterExerciseService.findByMasterEvaluationIDInAndActiveTrue(evaluationIds);

      List<ExerciseAssessment> assessments = new ArrayList<ExerciseAssessment>();
      for (MasterExercise exercise : masterExerciseList) {
        ExerciseAssessment assessment = new ExerciseAssessment();
        assessment.setMasterExerciseID(exercise.getId());
        assessment.setProjectAssessmentID(projectAssessment.getId());
        assessment.setSelected(false);
        assessment.setApprovedScore(0.0f);
        assessment.setSubmittedScore(0.0f);
        assessment.setScoreModifier(0.0f);
        assessment.setCreatedBy("system");
        assessment.setCreatedAt(newDate);

        assessments.add(assessment);
      }

      Iterable<ExerciseAssessment>  exerciseAssessmentIterable = exerciseAssessmentService.saveAll(assessments);
      assessments = StreamSupport.stream(exerciseAssessmentIterable.spliterator(), false).collect(Collectors.toList());

      List<Integer> exerciseIds = masterExerciseList.stream().map(MasterExercise::getId).collect(Collectors.toList());

      List<MasterCriteria> masterCriteriaList = masterCriteriaService.findByMasterExerciseIDInAndActiveTrue(exerciseIds);

      List<CriteriaScoring> scorings = new ArrayList<CriteriaScoring>();
      for (MasterCriteria criteria : masterCriteriaList) {

        ExerciseAssessment assmnt = assessments.stream()
                                               .filter(assessment -> assessment.getMasterExerciseID() == criteria.getMasterExerciseID())
                                               .findFirst()
                                               .get();
        CriteriaScoring scoring = new CriteriaScoring();
        scoring.setExerciseAssessmentID(assmnt.getId());
        scoring.setMasterCriteriaID(criteria.getId());
        scoring.setProjectAssessmentID(projectAssessment.getId());
        scoring.setSelected(false);
        scoring.setApprovedScore(0.0f);
        scoring.setSubmittedScore(0.0f);
        scoring.setApprovalStatus(1); // idle
        scoring.setCreatedBy("system");
        scoring.setCreatedAt(newDate);

        scorings.add(scoring);
      }

      Iterable<CriteriaScoring> criteriaScorings = criteriaScoringService.saveAll(scorings);

      List<DocumentFile> docFiles = new ArrayList<DocumentFile>();
      for (CriteriaScoring cs :  criteriaScorings) {
        List<MasterDocument> masterDocuments = masterDocumentService.findBymasterCriteriaIDAndActiveTrue(cs.getMasterCriteriaID());
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

  @Override
  public TransactionCreationResponse createFATransactionForProcessInstance(String processInstanceID) {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    try {
      String activityInstanceId = runtimeService.getActivityInstance(processInstanceID).getId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "process instance not found");

      return new TransactionCreationResponse(false, map);
    }

    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, "FA");
    if (projectAssessments.size() > 0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "FA Transaction already created");

      return new TransactionCreationResponse(false, map);
    }

    try {
      MasterAdmin masterAdmin = masterAdminService.findLast();
      MasterTemplate masterTemplate = masterTemplateService.findById(masterAdmin.getFaTemplateID());
      Date newDate = new Date();

      ProjectAssessment projectAssessment = new ProjectAssessment();
      projectAssessment.setMasterTemplateID(masterTemplate.getId());
      projectAssessment.setProcessInstanceID(processInstanceID);
      projectAssessment.setProjectName(masterTemplate.getProjectVersion());
      projectAssessment.setPotentialScore(0.0f);
      projectAssessment.setApprovedScore(0.0f);
      projectAssessment.setSubmittedScore(0.0f);
      projectAssessment.setScoreModifier(0.0f);
      projectAssessment.setCreatedAt(newDate);
      projectAssessment.setAssessmentType("FA");
      projectAssessment.setProposedLevelID(masterAdmin.getDefaultFALevel());

      projectAssessment = projectAssessmentService.save(projectAssessment);

      List<Integer> evaluationIds = masterEvaluationService.getAllIdsByTemplateIdAndActiveTrue(masterTemplate.getId());

      List<MasterExercise> masterExerciseList = masterExerciseService.findByMasterEvaluationIDInAndActiveTrue(evaluationIds);

      List<ExerciseAssessment> assessments = new ArrayList<ExerciseAssessment>();
      for (MasterExercise exercise : masterExerciseList) {
        ExerciseAssessment assessment = new ExerciseAssessment();
        assessment.setMasterExerciseID(exercise.getId());
        assessment.setProjectAssessmentID(projectAssessment.getId());
        assessment.setSelected(false);
        assessment.setApprovedScore(0.0f);
        assessment.setSubmittedScore(0.0f);
        assessment.setScoreModifier(0.0f);
        assessment.setCreatedBy("system");
        assessment.setCreatedAt(newDate);

        assessments.add(assessment);
      }

      Iterable<ExerciseAssessment>  exerciseAssessmentIterable = exerciseAssessmentService.saveAll(assessments);
      assessments = StreamSupport.stream(exerciseAssessmentIterable.spliterator(), false).collect(Collectors.toList());

      List<Integer> exerciseIds = masterExerciseList.stream().map(MasterExercise::getId).collect(Collectors.toList());

      List<MasterCriteria> masterCriteriaList = masterCriteriaService.findByMasterExerciseIDInAndActiveTrue(exerciseIds);

      List<CriteriaScoring> scorings = new ArrayList<CriteriaScoring>();
      for (MasterCriteria criteria : masterCriteriaList) {

        ExerciseAssessment assmnt = assessments.stream()
                                               .filter(assessment -> assessment.getMasterExerciseID() == criteria.getMasterExerciseID())
                                               .findFirst()
                                               .get();
        CriteriaScoring scoring = new CriteriaScoring();
        scoring.setExerciseAssessmentID(assmnt.getId());
        scoring.setMasterCriteriaID(criteria.getId());
        scoring.setProjectAssessmentID(projectAssessment.getId());
        scoring.setSelected(false);
        scoring.setApprovedScore(0.0f);
        scoring.setSubmittedScore(0.0f);
        scoring.setApprovalStatus(1); // idle
        scoring.setCreatedBy("system");
        scoring.setCreatedAt(newDate);

        scorings.add(scoring);
      }

      Iterable<CriteriaScoring> criteriaScorings = criteriaScoringService.saveAll(scorings);

      List<DocumentFile> docFiles = new ArrayList<DocumentFile>();
      for (CriteriaScoring cs :  criteriaScorings) {
        List<MasterDocument> masterDocuments = masterDocumentService.findBymasterCriteriaIDAndActiveTrue(cs.getMasterCriteriaID());
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
    resultMap.put("message", "FA transaction has been created");
    return new TransactionCreationResponse(true, resultMap);
  }

  @Override
  public TransactionCreationResponse tagSubmittedAttachment(String processInstanceID, Integer masterTemplateID) {
    Date newDate = new Date();
    List<Attachment> attachments = attachmentService.findByProcessInstanceIdAndMasterTemplateId(processInstanceID, masterTemplateID);
    attachments.forEach(attachment -> attachment.setSubmittedAt(newDate));
    attachmentService.saveAll(attachments);

    Map<String, String> resultMap = new HashMap<String, String>();
    resultMap.put("message", "Attachment has been tagged");
    return new TransactionCreationResponse(true, resultMap);
  }

  @Override
  public TransactionCreationResponse tagSubmittedAttachmentByAssessmentType(String processInstanceID, String assessmentType) {
    Date newDate = new Date();
    List<Attachment> attachments = attachmentService.findByProcessInstanceIdAndAssessmentType(processInstanceID, assessmentType);
    attachments.forEach(attachment -> attachment.setSubmittedAt(newDate));
    attachmentService.saveAll(attachments);

    Map<String, String> resultMap = new HashMap<String, String>();
    resultMap.put("message", "Attachment has been tagged");
    return new TransactionCreationResponse(true, resultMap);
  }
  
}
