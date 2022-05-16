package com.sbh.bpm.repository;


import com.sbh.bpm.model.ProjectUser;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectUserRepository extends CrudRepository<ProjectUser, Integer> {
  ProjectUser findByUserId(String userId);
}
