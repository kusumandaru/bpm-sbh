package com.sbh.bpm.repository;


import com.sbh.bpm.model.Group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<Group, String> {
  @Query(value = "select grp.ID_, grp.REV_, grp.NAME_, grp.TYPE_ FROM ACT_ID_GROUP as grp JOIN ACT_ID_MEMBERSHIP as membership ON grp.ID_ = membership.GROUP_ID_ WHERE USER_ID_= :userId", nativeQuery = true)
  Group getGroupByUserId(@Param("userId") String userId);
}
