package com.sbh.bpm.model;

import java.util.Date;

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
@Table(name = "activity_names")
public class ActivityName {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_certification_type_id")
  @Column(name="master_certification_type_id")
  @Getter
  @Setter
  private Integer masterCertificationTypeID;

  @SerializedName("activity_id")
  @Column(name="activity_id")
  @Getter
  @Setter
  private String activityID;

  @SerializedName("name")
  @Column(name="name")
  @Getter
  @Setter
  private String name;

  @SerializedName("active")
  @Column(name="active")
  @Getter
  @Setter
  private Boolean active;

  @SerializedName("created_at")
  @Column(name="created_at")
  @Getter
  @Setter
  private Date createdAt;

  @SerializedName("updated_at")
  @Column(name="updated_at")
  @Getter
  @Setter
  private Date updatedAt; 

  @SerializedName("created_by")
  @Column(name="created_by")
  @Getter
  @Setter
  private String createdBy;

  public ActivityName() {
  }

  public ActivityName(Integer id, Integer masterCertificationTypeID) {
    this.id = id;
    this.masterCertificationTypeID = masterCertificationTypeID;
  }
}
