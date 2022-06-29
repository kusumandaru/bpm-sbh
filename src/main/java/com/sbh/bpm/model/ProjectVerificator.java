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
@Table(name = "project_verificators")
public class ProjectVerificator {
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

  @SerializedName("group_id")
  @Column(name="group_id")
  @Getter
  @Setter
  private String groupId;

  public ProjectVerificator() {
  }

  public ProjectVerificator(String userId, String groupId, String processInstanceID, String invitedBy) {
    this.userId = userId;
    this.processInstanceID = processInstanceID;
    this.groupId = groupId;
    this.invitedBy = invitedBy;
  }
}
