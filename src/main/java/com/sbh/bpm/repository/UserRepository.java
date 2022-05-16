package com.sbh.bpm.repository;


import java.util.List;

import com.sbh.bpm.model.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
  @Query(value = "select user.ID_, user.REV_, user.FIRST_, user.LAST_, user.EMAIL_, user.ACTIVE_, user.AVATAR_URL_, user.TENANT_OWNER_ FROM ACT_ID_USER as user JOIN ACT_ID_TENANT_MEMBER as tenant_member ON user.ID_ = tenant_member.USER_ID_ WHERE tenant_member.TENANT_ID_= :tenantId", nativeQuery = true)
  List<User> findByTenantId(String tenantId);
}
