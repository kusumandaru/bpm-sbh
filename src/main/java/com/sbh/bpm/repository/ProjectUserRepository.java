package com.sbh.bpm.repository;


import java.util.List;

import com.sbh.bpm.model.ProjectUser;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectUserRepository extends CrudRepository<ProjectUser, Integer> {
  List<ProjectUser> findByUserId(String userId);
  List<ProjectUser> findByTenantId(String tenantId);
  List<ProjectUser> findByUserIdAndProcessInstanceID(String userId, String processInstanceID);
}
