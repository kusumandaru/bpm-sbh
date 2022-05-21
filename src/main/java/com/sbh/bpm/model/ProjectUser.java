package com.sbh.bpm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_users")
public class ProjectUser {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("user_id")
  @Column(name="user_id")
  @Getter
  @Setter
  private String userId;

  @SerializedName("invited_by")
  @Column(name="invited_by")
  @Getter
  @Setter
  private String invitedBy;

  @SerializedName("process_instance_id")
  @Column(name="process_instance_id")
  @Getter
  @Setter
  private String processInstanceID;

  @SerializedName("tenant_id")
  @Column(name="tenant_id")
  @Getter
  @Setter
  private String tenantId;

  @SerializedName("owner")
  @Column(name="owner")
  @Getter
  @Setter
  private Boolean owner;

  public ProjectUser() {
  }

  public ProjectUser(String userId, String tenantId, String processInstanceID, String invitedBy, Boolean owner) {
    this.userId = userId;
    this.processInstanceID = processInstanceID;
    this.tenantId = tenantId;
    this.invitedBy = invitedBy;
    this.owner = owner;
  }
}
