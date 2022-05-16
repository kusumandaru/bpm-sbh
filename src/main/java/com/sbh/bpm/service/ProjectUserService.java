package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectUser;
import com.sbh.bpm.repository.ProjectUserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ProjectUserService implements IProjectUserService{

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private ProjectUserRepository projectUserRepository;

  @Override
  public List<ProjectUser> findAll() {
    return (List<ProjectUser>) projectUserRepository.findAll();
  }

  @Override
  public ProjectUser findById(Integer projectUserId) {
    return projectUserRepository.findById(projectUserId).get();
  }

  @Override
  public ProjectUser findByUserId(String userId) {
    return projectUserRepository.findByUserId(userId);
  }

  @Override
  public ProjectUser save(ProjectUser projectUser) {
    return projectUserRepository.save(projectUser);
  }
}
