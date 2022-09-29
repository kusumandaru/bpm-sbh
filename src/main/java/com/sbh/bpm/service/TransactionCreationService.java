package com.sbh.bpm.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.sbh.bpm.model.Attachment;
import com.sbh.bpm.model.CriteriaScoring;
import com.sbh.bpm.model.DocumentFile;
import com.sbh.bpm.model.ExerciseAssessment;
import com.sbh.bpm.model.ExerciseScoreModifier;
import com.sbh.bpm.model.MasterCriteria;
import com.sbh.bpm.model.MasterDocument;
import com.sbh.bpm.model.MasterExercise;
import com.sbh.bpm.model.MasterLevel;
import com.sbh.bpm.model.MasterScoreModifier;
import com.sbh.bpm.model.MasterTemplate;
import com.sbh.bpm.model.ProjectAssessment;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import lombok.Setter;

@Service
@Transactional
public class TransactionCreationService implements ITransactionCreationService {
  private static final Logger logger = LoggerFactory.getLogger(TransactionCreationService.class);

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
  private IMasterLevelService masterLevelService;

  @Autowired
  private IProjectAssessmentService projectAssessmentService;

  @Autowired
  private ICriteriaScoringService criteriaScoringService;

  @Autowired
  private IExerciseAssessmentService exerciseAssessmentService;

  @Autowired
  private IMasterScoreModifierService masterScoreModifierService;

  @Autowired
  private IExerciseScoreModifierService exerciseScoreModifierService;

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
    TaskService taskService = processEngine.getTaskService();

    try {
      runtimeService.getActivityInstance(processInstanceID).getId();
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
      Task task = taskService.createTaskQuery().processInstanceId(processInstanceID).orderByTaskCreateTime().desc().singleResult();
      Integer certificationTypeId = (Integer) taskService.getVariable(task.getId(), "certification_type_id");
      //TODO: create if certifitionType ID null
      List<MasterTemplate> masterTemplates = masterTemplateService.findByMasterCertificationTypeID(certificationTypeId);
      MasterTemplate template = masterTemplates.stream().filter(t -> t.getProjectType().equals("design_recognition")).findFirst().get();
      Date newDate = new Date();

      ProjectAssessment projectAssessment = new ProjectAssessment();
      projectAssessment.setMasterTemplateID(template.getId());
      projectAssessment.setProcessInstanceID(processInstanceID);
      projectAssessment.setProjectName(template.getProjectVersion());
      projectAssessment.setPotentialScore(0.0f);
      projectAssessment.setApprovedScore(0.0f);
      projectAssessment.setSubmittedScore(0.0f);
      projectAssessment.setCreatedAt(newDate);
      projectAssessment.setAssessmentType("DR");

      projectAssessment = projectAssessmentService.save(projectAssessment);

      List<Integer> evaluationIds = masterEvaluationService.getAllIdsByTemplateIdAndActiveTrue(template.getId());

      List<MasterExercise> masterExerciseList = masterExerciseService.findByMasterEvaluationIDInAndActiveTrue(evaluationIds);

      List<ExerciseAssessment> assessments = new ArrayList<ExerciseAssessment>();

      for (MasterExercise exercise : masterExerciseList) {
        ExerciseAssessment assessment = new ExerciseAssessment();
        assessment.setMasterExerciseID(exercise.getId());
        assessment.setProjectAssessmentID(projectAssessment.getId());
        assessment.setSelected(false);
        assessment.setApprovedScore(0.0f);
        assessment.setSubmittedScore(0.0f);
        assessment.setCreatedBy("system");
        assessment.setCreatedAt(newDate);

        assessments.add(assessment);
      }

      Iterable<ExerciseAssessment>  exerciseAssessmentIterable = exerciseAssessmentService.saveAll(assessments);
      assessments = StreamSupport.stream(exerciseAssessmentIterable.spliterator(), false).collect(Collectors.toList());

      List<Integer> exerciseIds = masterExerciseList.stream().map(MasterExercise::getId).collect(Collectors.toList());

      List<MasterCriteria> masterCriteriaList = masterCriteriaService.findByMasterExerciseIDInAndActiveTrue(exerciseIds);

      List<CriteriaScoring> scorings = new ArrayList<CriteriaScoring>();

      List<ExerciseScoreModifier> modifiers = new ArrayList<ExerciseScoreModifier>();

      List<MasterScoreModifier> masterScoreModifiers = masterScoreModifierService.findByMasterExerciseIDIn(exerciseIds);
      for (MasterScoreModifier masterModifier : masterScoreModifiers) {
        ExerciseAssessment assmnt = assessments.stream()
                                               .filter(assessment -> assessment.getMasterExerciseID() == masterModifier.getMasterExerciseID())
                                               .findFirst()
                                               .get();

        ExerciseScoreModifier modifier = new ExerciseScoreModifier();
        modifier.setMasterScoreModifierID(masterModifier.getId());
        modifier.setProjectAssessmentID(projectAssessment.getId());
        modifier.setExerciseAssessmentID(assmnt.getId());
        modifier.setScoreModifier(masterModifier.getScoreModifier());
        modifier.setEnabled(false);
        modifier.setCreatedAt(newDate);
        modifier.setCreatedBy("system");

        modifiers.add(modifier);
      }

      Iterable<ExerciseScoreModifier> scoreModifiers = exerciseScoreModifierService.saveAll(modifiers);

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
        scoring.setAdditionalNotes(criteria.getAdditionalNotes());
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

      documentFileService.saveAll(docFiles); 

      MasterLevel level = getMinimumLevelFromProjectAssessmentID(projectAssessment.getId());
      projectAssessment.setProposedLevelID(level.getId());
      projectAssessment.setTargetScore(level.getScore());

      projectAssessment = projectAssessmentService.save(projectAssessment);
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      Map<String, String> map = new HashMap<String, String>();
      ex.printStackTrace();
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
    TaskService taskService = processEngine.getTaskService();

    try {
      runtimeService.getActivityInstance(processInstanceID).getId();
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
      Task task = taskService.createTaskQuery().processInstanceId(processInstanceID).orderByTaskCreateTime().desc().singleResult();
      Integer certificationTypeId = (Integer) taskService.getVariable(task.getId(), "certification_type_id");
      List<MasterTemplate> masterTemplates = masterTemplateService.findByMasterCertificationTypeID(certificationTypeId);
      MasterTemplate template = masterTemplates.stream().filter(t -> t.getProjectType().equals("final_assessment")).findFirst().get();

      Date newDate = new Date();

      ProjectAssessment projectAssessment = new ProjectAssessment();
      projectAssessment.setMasterTemplateID(template.getId());
      projectAssessment.setProcessInstanceID(processInstanceID);
      projectAssessment.setProjectName(template.getProjectVersion());
      projectAssessment.setPotentialScore(0.0f);
      projectAssessment.setApprovedScore(0.0f);
      projectAssessment.setSubmittedScore(0.0f);
      projectAssessment.setCreatedAt(newDate);
      projectAssessment.setAssessmentType("FA");

      projectAssessment = projectAssessmentService.save(projectAssessment);

      List<Integer> evaluationIds = masterEvaluationService.getAllIdsByTemplateIdAndActiveTrue(template.getId());

      List<MasterExercise> masterExerciseList = masterExerciseService.findByMasterEvaluationIDInAndActiveTrue(evaluationIds);

      List<ExerciseAssessment> assessments = new ArrayList<ExerciseAssessment>();
      for (MasterExercise exercise : masterExerciseList) {
        ExerciseAssessment assessment = new ExerciseAssessment();
        assessment.setMasterExerciseID(exercise.getId());
        assessment.setProjectAssessmentID(projectAssessment.getId());
        assessment.setSelected(false);
        assessment.setApprovedScore(0.0f);
        assessment.setSubmittedScore(0.0f);
        assessment.setCreatedBy("system");
        assessment.setCreatedAt(newDate);

        assessments.add(assessment);
      }

      Iterable<ExerciseAssessment>  exerciseAssessmentIterable = exerciseAssessmentService.saveAll(assessments);
      assessments = StreamSupport.stream(exerciseAssessmentIterable.spliterator(), false).collect(Collectors.toList());

      List<Integer> exerciseIds = masterExerciseList.stream().map(MasterExercise::getId).collect(Collectors.toList());

      List<MasterCriteria> masterCriteriaList = masterCriteriaService.findByMasterExerciseIDInAndActiveTrue(exerciseIds);

      List<CriteriaScoring> scorings = new ArrayList<CriteriaScoring>();

      List<ExerciseScoreModifier> modifiers = new ArrayList<ExerciseScoreModifier>();

      List<MasterScoreModifier> masterScoreModifiers = masterScoreModifierService.findByMasterExerciseIDIn(exerciseIds);
      for (MasterScoreModifier masterModifier : masterScoreModifiers) {
        ExerciseAssessment assmnt = assessments.stream()
                                               .filter(assessment -> assessment.getMasterExerciseID() == masterModifier.getMasterExerciseID())
                                               .findFirst()
                                               .get();

        ExerciseScoreModifier modifier = new ExerciseScoreModifier();
        modifier.setMasterScoreModifierID(masterModifier.getId());
        modifier.setProjectAssessmentID(projectAssessment.getId());
        modifier.setExerciseAssessmentID(assmnt.getId());
        modifier.setScoreModifier(masterModifier.getScoreModifier());
        modifier.setEnabled(false);
        modifier.setCreatedAt(newDate);
        modifier.setCreatedBy("system");

        modifiers.add(modifier);
      }

      Iterable<ExerciseScoreModifier> scoreModifiers = exerciseScoreModifierService.saveAll(modifiers);

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
        scoring.setAdditionalNotes(criteria.getAdditionalNotes());
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

      documentFileService.saveAll(docFiles);

      MasterLevel level = getMinimumLevelFromProjectAssessmentID(projectAssessment.getId());
      projectAssessment.setProposedLevelID(level.getId());
      projectAssessment.setTargetScore(level.getScore());

      projectAssessment = projectAssessmentService.save(projectAssessment);
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      Map<String, String> map = new HashMap<String, String>();
      map.put("message", ex.getMessage());
      ex.printStackTrace();

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

  @Override
  public ProjectAssessment calculateProjectAssessment(Integer projectAssessmentId) {
    List<CriteriaScoring> allScorings = criteriaScoringService.findByProjectAssessmentID(projectAssessmentId);

    List<ExerciseAssessment> allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    List<ExerciseScoreModifier> allScoreModifiers = exerciseScoreModifierService.findByProjectAssessmentID(projectAssessmentId);

    for(ExerciseAssessment assessment : allAssessments) {
      if(assessment.getExercise().getMaxScore() == null){
        continue;
       }
      Integer assessmentID = assessment.getId();
      Float scoreModifier = allScoreModifiers.stream()
                                             .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                             .filter(score -> score.getEnabled().equals(true))
                                             .map(ExerciseScoreModifier::getScoreModifier).reduce(0.0f, Float::sum);
      Float approvedScore = allScorings.stream()
                                       .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                       .map(CriteriaScoring::getApprovedScore).reduce(0.0f, Float::sum);
      Float submittedScore = allScorings.stream()
                                        .filter(score -> score.getExerciseAssessmentID().equals(assessmentID))
                                        .map(CriteriaScoring::getSubmittedScore).reduce(0.0f, Float::sum);
      Float maxScore = scoreModifier + (float) assessment.getExercise().getMaxScore();
      if (approvedScore > maxScore) {
        approvedScore = maxScore;
      }
      if (submittedScore > maxScore) {
        submittedScore = maxScore;
      }
      assessment.setApprovedScore(approvedScore);
      assessment.setSubmittedScore(submittedScore);
      assessment = exerciseAssessmentService.save(assessment);
    }

    allAssessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentId);
    Float approvedScore = allAssessments.stream()
                                       .map(ExerciseAssessment::getApprovedScore).reduce(0.0f, Float::sum);
    Float submittedScore = allAssessments.stream()
                                        .map(ExerciseAssessment::getSubmittedScore).reduce(0.0f, Float::sum);
    Integer totalScore = Math.round(submittedScore + approvedScore);

    ProjectAssessment projectAssessment = projectAssessmentService.findById(projectAssessmentId);
    List<MasterLevel> allLevels = getAllLevelFromProjectAssessmentID(projectAssessment.getMasterTemplateID());
    List<MasterLevel> filteredLevels = allLevels.stream()
                        .filter(l -> l.getScore() <= totalScore )
                        .sorted(Comparator.comparingDouble(MasterLevel::getPercentage))
                        .collect(Collectors.toList());

    MasterLevel level;
    if (filteredLevels.isEmpty()) {
      level = masterLevelService.findFirstByMasterTemplateIDOrderByPercentageAsc(projectAssessment.getMasterTemplateID());
    } else {
      level = filteredLevels.get(filteredLevels.size() - 1);
    }
    projectAssessment.setSubmittedScore(submittedScore);
    projectAssessment.setApprovedScore(approvedScore);
    projectAssessment.setProposedLevelID(level.getId());
    projectAssessment.setTargetScore(level.getScore());

    projectAssessment = projectAssessmentService.save(projectAssessment);

    return projectAssessment;
  }

  @Override
  public MasterLevel getMinimumLevelFromProjectAssessmentID(Integer projectAssessmentID) {
    return getAllLevelFromProjectAssessmentID(projectAssessmentID).get(0);
  }

  @Override
  public List<MasterLevel> getAllLevelFromProjectAssessmentID(Integer projectAssessmentID) {
    List<ExerciseAssessment> assessments = exerciseAssessmentService.findByProjectAssessmentID(projectAssessmentID);
    Integer sumMaxScore = assessments.stream().
                          filter(f-> f.getExercise() != null).
                          map(ExerciseAssessment::getExercise).
                          filter(f-> f.getExerciseType().equals("score")).
                          filter(f-> f.getBonusPoint().equals(false)).
                          map(MasterExercise::getMaxScore).
                          collect(Collectors.summingInt(Integer::intValue));

    ProjectAssessment pa = projectAssessmentService.findById(projectAssessmentID);
    List<MasterLevel> masterLevels = masterLevelService.findByMasterTemplateID(pa.getMasterTemplateID());
    masterLevels.stream().sorted(Comparator.comparingDouble(MasterLevel::getPercentage)).forEach(l -> {
      Float scr = sumMaxScore * l.getPercentage() / 100;
      if (l.getRounddown()) {
        l.setScore((int) Math.floor(scr));
      } else {
        l.setScore(Math.round(scr));
      }
    });

    return masterLevels;
  }
}
