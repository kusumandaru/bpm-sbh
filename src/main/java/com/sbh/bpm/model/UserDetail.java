package com.sbh.bpm.model;

import com.google.gson.annotations.SerializedName;

import org.camunda.bpm.engine.identity.Tenant;

import lombok.Getter;
import lombok.Setter;

public class UserDetail {
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

  @Getter @Setter
  private Group group;

  @Getter @Setter
  private Tenant tenant;

  public static UserDetail CreateFromUser(User user, Tenant tenant, Group group) {
    UserDetail userDetail = new UserDetail();
    userDetail.setUsername(user.getId());
    userDetail.setEmail(user.getEmail());
    userDetail.setFirstName(user.getFirstName());
    userDetail.setLastName(user.getLastName());
    userDetail.setFullName(user.getFirstName() + " " + user.getLastName());
    userDetail.setActive(user.getActive());
    userDetail.setAvatarUrl(user.getAvatarUrl());
    userDetail.setTenant(tenant);
    userDetail.setGroup(group);

    return userDetail;
  }
}
