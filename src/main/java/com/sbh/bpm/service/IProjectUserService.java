package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectUser;

public interface IProjectUserService {
  List<ProjectUser> findAll();
  ProjectUser findById(Integer projectUserId);
  ProjectUser findByUserId(String userId);
  ProjectUser save(ProjectUser projectUser);
}
