package com.sbh.bpm.model;

import javax.persistence.Column;

import com.google.gson.annotations.SerializedName;

import org.camunda.bpm.engine.identity.Tenant;

import lombok.Getter;
import lombok.Setter;

public class UserDetail {
  @Getter @Setter
  private String id;

  @Getter @Setter
  private String username;

  @SerializedName("first_name")
  @Getter @Setter
  private String firstName;

  @SerializedName("last_name")
  @Getter @Setter
  private String lastName;

  @Getter @Setter
  private String email;

  @SerializedName("full_name")
  @Getter @Setter
  private String fullName;

  @Getter
  @Setter
  @SerializedName("active")
  private Boolean active;

  @Getter
  @Setter
  @SerializedName("avatar_url")
  private String avatarUrl;

  @Getter
  @Setter
  @SerializedName("tenant_owner")
  @Column(name="TENANT_OWNER_")
  private Boolean tenantOwner;

  @Getter @Setter
  private Group group;

  @Getter @Setter
  private Tenant tenant;

  @Getter
  @Setter
  @SerializedName("group_id")
  private String groupId;

  @Getter
  @Setter
  @SerializedName("tenant_id")
  private String tenantId;

  public static UserDetail CreateFromUser(User user, Tenant tenant, Group group) {
    UserDetail userDetail = new UserDetail();
    userDetail.setId(user.getId());
    userDetail.setUsername(user.getId());
    userDetail.setEmail(user.getEmail());
    userDetail.setFirstName(user.getFirstName());
    userDetail.setLastName(user.getLastName());
    userDetail.setFullName(user.getFirstName() + " " + user.getLastName());
    userDetail.setActive(user.getActive());
    userDetail.setAvatarUrl(user.getAvatarUrl());
    userDetail.setTenantOwner(user.getTenantOwner());
    userDetail.setTenant(tenant);
    userDetail.setGroup(group);
    if (group != null) {
      userDetail.setGroupId(group.getId());
    }
    if (tenant != null) {
      userDetail.setTenantId(tenant.getId());
    }

    return userDetail;
  }
}
