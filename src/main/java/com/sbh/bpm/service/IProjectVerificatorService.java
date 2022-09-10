package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectVerificator;
import com.sbh.bpm.model.UserDetail;

public interface IProjectVerificatorService {
  List<ProjectVerificator> findAll();
  ProjectVerificator findById(Integer projectVerificatorId);
  List<ProjectVerificator> findByUserId(String userId);
  List<ProjectVerificator> findByUserIdAndProcessInstanceID(String userId, String processInstanceID);
  List<ProjectVerificator> findByProcessInstanceID(String processInstanceID);
  List<ProjectVerificator> findByGroupId(String groupId);
  ProjectVerificator save(ProjectVerificator projectVerificator);
  Iterable<ProjectVerificator> saveAll(Iterable<ProjectVerificator> projectVerificators);
  void deleteAll(Iterable<ProjectVerificator> projectVerificators);
  List<ProjectVerificator> assignProjectVerificators(UserDetail userDetail, String userId, String projectIds);
  void delete(ProjectVerificator projectVerificator);
}
