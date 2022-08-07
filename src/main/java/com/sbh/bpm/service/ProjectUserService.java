package com.sbh.bpm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sbh.bpm.model.ProjectUser;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.repository.ProjectUserRepository;

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
public class ProjectUserService implements IProjectUserService{

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private ProjectUserRepository projectUserRepository;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Override
  public List<ProjectUser> findAll() {
    return (List<ProjectUser>) projectUserRepository.findAll();
  }

  @Override
  public ProjectUser findById(Integer projectUserId) {
    return projectUserRepository.findById(projectUserId).get();
  }

  @Override
  public List<ProjectUser> findByUserId(String userId) {
    return projectUserRepository.findByUserId(userId);
  }

  @Override
  public ProjectUser save(ProjectUser projectUser) {
    return projectUserRepository.save(projectUser);
  }

  @Override
  public Iterable<ProjectUser> saveAll(Iterable<ProjectUser> projectUsers) {
    return projectUserRepository.saveAll(projectUsers);
  }

  @Override
  public void deleteAll(Iterable<ProjectUser> projectUsers) {
    projectUserRepository.deleteAll(projectUsers);
  }

  @Override
  public void delete(ProjectUser projectUser) {
    projectUserRepository.delete(projectUser);
  }

  @Override
  public List<ProjectUser> assignProjectUsers(UserDetail userDetail, String userId, String projectIds) {
    TransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
    
    List<ProjectUser> existingProjectUsers = findByUserId(userId);
    List<String> existingProjectUserIds = existingProjectUsers.stream().map(project -> project.getProcessInstanceID()).collect(Collectors.toList());
    
    String[] projectIdArray = projectIds.split(",");
    List<String> projectIdList = Arrays.asList(projectIdArray);

    List<String> deletedProjectIds = new ArrayList<>(existingProjectUserIds);
    deletedProjectIds.removeAll(projectIdList);
    deletedProjectIds.removeIf(item -> item == null || "".equals(item));

    List<String> newProjectIds = new ArrayList<>(projectIdList);
    newProjectIds.removeAll(existingProjectUserIds);
    newProjectIds.removeIf(item -> item == null || "".equals(item));

    try {
      Iterable<ProjectUser> deletedIterable = existingProjectUsers.stream().filter(project -> deletedProjectIds.contains(project.getProcessInstanceID())).collect(Collectors.toList());
      deleteAll(deletedIterable);

      Iterable<ProjectUser> newIterable = newProjectIds.stream().map(processInstanceID -> new ProjectUser(userId, userDetail.getTenant().getId(), processInstanceID, userDetail.getUsername(), false)).collect(Collectors.toList());
      saveAll(newIterable);
    } catch(Exception ex) {
      transactionManager.rollback(transactionStatus);

      throw ex;
    }
    return findByUserId(userId);
  }

  @Override
  public List<ProjectUser> findByTenantId(String tenantId) {
    return projectUserRepository.findByTenantId(tenantId);
  }

  @Override
  public List<ProjectUser> findByUserIdAndProcessInstanceID(String userId, String processInstanceID) {
    return projectUserRepository.findByUserIdAndProcessInstanceID(userId, processInstanceID);
  }

  @Override
  public List<ProjectUser> findByProcessInstanceID(String processInstanceID) {
    return projectUserRepository.findByProcessInstanceID(processInstanceID);
  }
}
