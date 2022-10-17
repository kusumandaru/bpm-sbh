package com.sbh.bpm.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "master_evaluations")
public class MasterEvaluation {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_template_id")
  @Column(name="master_template_id")
  @Getter
  @Setter
  private Integer masterTemplateID;

  @SerializedName("code")
  @Column(name="code")
  @Getter
  @Setter
  private String code;

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

  @Transient
  @Getter
  @Setter
  private List<ExerciseAssessment> exercises;

  @SerializedName("approved_score")
  @Transient
  @Getter
  @Setter
  private Float approvedScore;

  @SerializedName("submitted_score")
  @Transient
  @Getter
  @Setter
  private Float submittedScore;

  public MasterEvaluation() {
  }

  public MasterEvaluation(Integer id, String code, String name, Integer masterTemplateID) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.masterTemplateID = masterTemplateID;
  }
}
