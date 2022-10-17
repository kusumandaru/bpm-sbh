package com.sbh.bpm.repository;


import java.util.List;

import com.sbh.bpm.model.ProjectVerificator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectVerificatorRepository extends CrudRepository<ProjectVerificator, Integer> {
  List<ProjectVerificator> findByUserId(String userId);
  List<ProjectVerificator> findByGroupId(String groupId);
  List<ProjectVerificator> findByUserIdAndProcessInstanceID(String userId, String processInstanceID);
  List<ProjectVerificator> findByProcessInstanceID(String processInstanceID);
}
