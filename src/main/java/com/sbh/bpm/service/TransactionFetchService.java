package com.sbh.bpm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sbh.bpm.model.Attachment;
import com.sbh.bpm.model.Comment;
import com.sbh.bpm.model.CriteriaScoring;
import com.sbh.bpm.model.DocumentFile;
import com.sbh.bpm.model.ExerciseAssessment;
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.model.MasterExercise;
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
public class TransactionFetchService implements ITransactionFetchService {

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
  private ICommentService commentService;

  @Autowired
  private IAttachmentService attachmentService;

  @Autowired
  private PlatformTransactionManager transactionManager;
  
  public class TransactionFetchResponse {
    @Getter
    @Setter
    public Boolean success;
    
    @Getter
    @Setter
    public Map<String, String> messages;

    @Getter
    @Setter
    public List<ProjectAssessment> projectAssessments;
  
    public TransactionFetchResponse(Boolean success, Map<String, String> messages, List<ProjectAssessment> projectAssessments) {
      this.success = success;
      this.messages = messages;
      this.projectAssessments = projectAssessments;
    }
  }

  @Override
  public TransactionFetchResponse GetDRTransactionForProcessInstance(String processInstanceID) {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    List<ProjectAssessment> projectAssessments = new ArrayList<ProjectAssessment>();
    try {
      String activityInstanceId = runtimeService.getActivityInstance(processInstanceID).getId();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "process instance not found");

      return new TransactionFetchResponse(false, map, projectAssessments);
    }

    try {
      projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, "DR");
      for (ProjectAssessment pa : projectAssessments) {
        List<MasterEvaluation> masterEvaluations = masterEvaluationService.findByMasterTemplateID(pa.getMasterTemplateID());
        List<ExerciseAssessment> exerciseAssessments = exerciseAssessmentService.findByProjectAssessmentID(pa.getId());
        List<CriteriaScoring> criteriaScorings = criteriaScoringService.findByProjectAssessmentID(pa.getId());

        List<Integer> criteriaScoringIds = criteriaScorings.stream().map(CriteriaScoring::getId).collect(Collectors.toList());
        List<Comment> comments = commentService.findByCriteriaScoringIDIn(criteriaScoringIds);
        List<DocumentFile> documentFiles = documentFileService.findByCriteriaScoringIDIn(criteriaScoringIds);
        List<Integer> documentFileIds = documentFiles.stream().map(DocumentFile::getId).collect(Collectors.toList());
        List<Attachment> attachments = attachmentService.findByDocumentFileIDIn(documentFileIds);

        for (CriteriaScoring criteriaScoring : criteriaScorings) {
          List<DocumentFile> docs = documentFiles.stream().filter(documentFile -> documentFile.getCriteriaScoringID().equals(criteriaScoring.getId())).collect(Collectors.toList());
          for(DocumentFile doc : docs) {
            List<Attachment> attchs = attachments.stream().filter(attachment -> attachment.getDocumentFileID().equals(doc.getId())).collect(Collectors.toList());
            doc.setAttachments(attchs);
          }
          criteriaScoring.setDocuments(docs);

          List<Comment> coms = comments.stream().filter(comment -> comment.getCriteriaScoringID().equals(criteriaScoring.getId())).collect(Collectors.toList());
          criteriaScoring.setComments(coms);
        }

        for (ExerciseAssessment exerciseAsessment : exerciseAssessments) {
          List<MasterCriteria> masterCriterias = masterCriteriaService.findByMasterExerciseID(exerciseAsessment.getMasterExerciseID());
          List<Integer> masterCriteriaIds = masterCriterias.stream().map(MasterCriteria::getId).collect(Collectors.toList());

          List<CriteriaScoring> crts = criteriaScorings.stream().filter(exercise -> masterCriteriaIds.contains(exercise.getMasterCriteriaID())).collect(Collectors.toList());
          exerciseAsessment.setCriterias(crts);
        }

        for (MasterEvaluation masterEvaluation : masterEvaluations) {
          List<MasterExercise> masterExercises = masterExerciseService.findByMasterEvaluationID(masterEvaluation.getId());
          List<Integer> masterExerciseIds = masterExercises.stream().map(MasterExercise::getId).collect(Collectors.toList());

          List<ExerciseAssessment> excs = exerciseAssessments.stream().filter(exercise -> masterExerciseIds.contains(exercise.getMasterExerciseID())).collect(Collectors.toList());
          masterEvaluation.setExercises(excs);
        }
        pa.setMasterEvaluations(masterEvaluations);
      }

    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      Map<String, String> map = new HashMap<String, String>();
      map.put("message", ex.getMessage());

      return new TransactionFetchResponse(false, map, projectAssessments);
    }

    Map<String, String> resultMap = new HashMap<String, String>();
    resultMap.put("message", "DR transaction has been loaded");
    return new TransactionFetchResponse(true, resultMap, projectAssessments);
  }

  @Override
  public TransactionFetchResponse GetFATransactionForProcessInstance(String processInstanceID) {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();

    List<ProjectAssessment> projectAssessments = new ArrayList<ProjectAssessment>();
    try {
      String activityInstanceId = runtimeService.getActivityInstance(processInstanceID).getId();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "process instance not found");

      return new TransactionFetchResponse(false, map, projectAssessments);
    }

    try {
      projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, "FA");
      for (ProjectAssessment pa : projectAssessments) {
        List<MasterEvaluation> masterEvaluations = masterEvaluationService.findByMasterTemplateID(pa.getMasterTemplateID());
        List<ExerciseAssessment> exerciseAssessments = exerciseAssessmentService.findByProjectAssessmentID(pa.getId());
        List<CriteriaScoring> criteriaScorings = criteriaScoringService.findByProjectAssessmentID(pa.getId());

        List<Integer> criteriaScoringIds = criteriaScorings.stream().map(CriteriaScoring::getId).collect(Collectors.toList());
        List<Comment> comments = commentService.findByCriteriaScoringIDIn(criteriaScoringIds);
        List<DocumentFile> documentFiles = documentFileService.findByCriteriaScoringIDIn(criteriaScoringIds);
        List<Integer> documentFileIds = documentFiles.stream().map(DocumentFile::getId).collect(Collectors.toList());
        List<Attachment> attachments = attachmentService.findByDocumentFileIDIn(documentFileIds);

        for (CriteriaScoring criteriaScoring : criteriaScorings) {
          List<DocumentFile> docs = documentFiles.stream().filter(documentFile -> documentFile.getCriteriaScoringID().equals(criteriaScoring.getId())).collect(Collectors.toList());
          for(DocumentFile doc : docs) {
            List<Attachment> attchs = attachments.stream().filter(attachment -> attachment.getDocumentFileID().equals(doc.getId())).collect(Collectors.toList());
            doc.setAttachments(attchs);
          }
          criteriaScoring.setDocuments(docs);

          List<Comment> coms = comments.stream().filter(comment -> comment.getCriteriaScoringID().equals(criteriaScoring.getId())).collect(Collectors.toList());
          criteriaScoring.setComments(coms);
        }

        for (ExerciseAssessment exerciseAsessment : exerciseAssessments) {
          List<MasterCriteria> masterCriterias = masterCriteriaService.findByMasterExerciseID(exerciseAsessment.getMasterExerciseID());
          List<Integer> masterCriteriaIds = masterCriterias.stream().map(MasterCriteria::getId).collect(Collectors.toList());

          List<CriteriaScoring> crts = criteriaScorings.stream().filter(exercise -> masterCriteriaIds.contains(exercise.getMasterCriteriaID())).collect(Collectors.toList());
          exerciseAsessment.setCriterias(crts);
        }

        for (MasterEvaluation masterEvaluation : masterEvaluations) {
          List<MasterExercise> masterExercises = masterExerciseService.findByMasterEvaluationID(masterEvaluation.getId());
          List<Integer> masterExerciseIds = masterExercises.stream().map(MasterExercise::getId).collect(Collectors.toList());

          List<ExerciseAssessment> excs = exerciseAssessments.stream().filter(exercise -> masterExerciseIds.contains(exercise.getMasterExerciseID())).collect(Collectors.toList());
          masterEvaluation.setExercises(excs);
        }
        pa.setMasterEvaluations(masterEvaluations);
      }

    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      Map<String, String> map = new HashMap<String, String>();
      map.put("message", ex.getMessage());

      return new TransactionFetchResponse(false, map, projectAssessments);
    }

    Map<String, String> resultMap = new HashMap<String, String>();
    resultMap.put("message", "DR transaction has been loaded");
    return new TransactionFetchResponse(true, resultMap, projectAssessments);
  }

  @Override
  public List<MasterEvaluation> GetEvaluationScoreForProcessInstance(String processInstanceID, String assessmentType) {
    List<ProjectAssessment> projectAssessments = projectAssessmentService.findByProcessInstanceIDAndAssessmentType(processInstanceID, assessmentType);
    List<Integer> masterTemplateIds = projectAssessments.stream().map(ProjectAssessment::getMasterTemplateID).collect(Collectors.toList());
    List<MasterEvaluation> evaluations = masterEvaluationService.findByMasterTemplateIDIn(masterTemplateIds);
    List<ExerciseAssessment> exerciseAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessments.get(0).getId());

    evaluations = evaluations.stream().map(masterEvaluation -> {
      List<MasterExercise> masterExercises = masterExerciseService.findByMasterEvaluationID(masterEvaluation.getId());
      List<Integer> masterExerciseIds = masterExercises.stream().map(MasterExercise::getId).collect(Collectors.toList());

      List<ExerciseAssessment> excs = exerciseAssessments.stream().filter(exercise -> masterExerciseIds.contains(exercise.getMasterExerciseID())).collect(Collectors.toList());
      Float evaluationApprovedScore = excs.stream().map(exercise -> exercise.getApprovedScore()).reduce(0.0f, Float::sum);
      Float evaluationSubmittedScore = excs.stream().map(exercise -> exercise.getSubmittedScore()).reduce(0.0f, Float::sum);
      masterEvaluation.setApprovedScore(evaluationApprovedScore);
      masterEvaluation.setSubmittedScore(evaluationSubmittedScore);
      
      return masterEvaluation;
    }).collect(Collectors.toList());

    return evaluations;
  }
}
