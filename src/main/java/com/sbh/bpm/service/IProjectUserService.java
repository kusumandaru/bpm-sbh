package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectUser;
import com.sbh.bpm.model.UserDetail;

public interface IProjectUserService {
  List<ProjectUser> findAll();
  ProjectUser findById(Integer projectUserId);
  List<ProjectUser> findByUserId(String userId);
  List<ProjectUser> findByUserIdAndProcessInstanceID(String userId, String processInstanceID);
  List<ProjectUser> findByTenantId(String tenantId);
  ProjectUser save(ProjectUser projectUser);
  Iterable<ProjectUser> saveAll(Iterable<ProjectUser> projectUsers);
  void deleteAll(Iterable<ProjectUser> projectUsers);
  List<ProjectUser> assignProjectUsers(UserDetail userDetail, String userId, String projectIds);
}
