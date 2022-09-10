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

  User findByEmail(String email);

  @Query(value = "select count(*) FROM ACT_ID_USER as user JOIN ACT_ID_TENANT_MEMBER as tenant_member ON user.ID_ = tenant_member.USER_ID_ WHERE tenant_member.TENANT_ID_= :tenantId", nativeQuery = true)
  Long countByTenantId(String tenantId);

  @Query(value = "select user.* FROM ACT_ID_USER as user JOIN password_tokens ON user.ID_ = password_tokens.user_id WHERE password_tokens.token= :token", nativeQuery = true)
  User findByToken(String token);

  @Query(value = "select user.ID_, user.REV_, user.FIRST_, user.LAST_, user.EMAIL_, user.ACTIVE_, user.AVATAR_URL_, user.TENANT_OWNER_ FROM ACT_ID_USER as user JOIN ACT_ID_MEMBERSHIP as membership ON user.ID_ = membership.USER_ID_ WHERE membership.GROUP_ID_= :groupId", nativeQuery = true)
  List<User> findByGroupId(String groupId);
}
