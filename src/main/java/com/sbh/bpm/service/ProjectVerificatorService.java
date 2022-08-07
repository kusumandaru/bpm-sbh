package com.sbh.bpm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sbh.bpm.model.ProjectVerificator;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.repository.ProjectVerificatorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@Service
@Transactional
public class ProjectVerificatorService implements IProjectVerificatorService{

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private ProjectVerificatorRepository projectVerificatorRepository;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Override
  public List<ProjectVerificator> findAll() {
    return (List<ProjectVerificator>) projectVerificatorRepository.findAll();
  }

  @Override
  public ProjectVerificator findById(Integer projectVerificatorId) {
    return projectVerificatorRepository.findById(projectVerificatorId).get();
  }

  @Override
  public List<ProjectVerificator> findByUserId(String userId) {
    return projectVerificatorRepository.findByUserId(userId);
  }

  @Override
  public ProjectVerificator save(ProjectVerificator projectVerificator) {
    return projectVerificatorRepository.save(projectVerificator);
  }

  @Override
  public Iterable<ProjectVerificator> saveAll(Iterable<ProjectVerificator> projectVerificators) {
    return projectVerificatorRepository.saveAll(projectVerificators);
  }

  @Override
  public void deleteAll(Iterable<ProjectVerificator> projectVerificators) {
    projectVerificatorRepository.deleteAll(projectVerificators);
  }

  @Override
  public List<ProjectVerificator> assignProjectVerificators(UserDetail userDetail, String userId, String projectIds) {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
    
    // List<ProjectVerificator> existingProjectVerificators = findByUserId(userId);
    List<ProjectVerificator> allProjectVerificators = findAll();
    List<String> existingProjectVerificatorIds = allProjectVerificators.stream().map(project -> project.getProcessInstanceID()).collect(Collectors.toList());
    
    String[] projectIdArray = projectIds.split(",");
    List<String> projectIdList = Arrays.asList(projectIdArray);

    List<String> deletedProjectIds = new ArrayList<>(existingProjectVerificatorIds);
    deletedProjectIds.removeAll(projectIdList);
    deletedProjectIds.removeIf(item -> item == null || "".equals(item));

    List<String> newProjectIds = new ArrayList<>(projectIdList);
    newProjectIds.removeAll(existingProjectVerificatorIds);
    newProjectIds.removeIf(item -> item == null || "".equals(item));

    try {
      Iterable<ProjectVerificator> deletedIterable = allProjectVerificators.stream().filter(project -> deletedProjectIds.contains(project.getProcessInstanceID())).collect(Collectors.toList());
      deleteAll(deletedIterable);

      Iterable<ProjectVerificator> newIterable = newProjectIds.stream().map(processInstanceID -> new ProjectVerificator(userId, userDetail.getGroup().getId(), processInstanceID, userDetail.getUsername())).collect(Collectors.toList());
      saveAll(newIterable);
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      throw ex;
    }
    return findByUserId(userId);
  }

  @Override
  public List<ProjectVerificator> findByGroupId(String groupId) {
    return projectVerificatorRepository.findByGroupId(groupId);
  }

  @Override
  public List<ProjectVerificator> findByUserIdAndProcessInstanceID(String userId, String processInstanceID) {
    return projectVerificatorRepository.findByUserIdAndProcessInstanceID(userId, processInstanceID);
  }
}
