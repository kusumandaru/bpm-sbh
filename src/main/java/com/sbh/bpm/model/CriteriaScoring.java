package com.sbh.bpm.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "criteria_scorings")
public class CriteriaScoring {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_criteria_id")
  @Column(name="master_criteria_id")
  @Getter
  @Setter
  private Integer masterCriteriaID;

  @SerializedName("project_assessment_id")
  @Column(name="project_assessment_id")
  @Getter
  @Setter
  private Integer projectAssessmentID;

  @SerializedName("selected")
  @Column(name="selected")
  @Getter
  @Setter
  private Boolean selected;

  @SerializedName("score")
  @Column(name="score")
  @Getter
  @Setter
  private Float score;

  @SerializedName("potential_score")
  @Column(name="potential_score")
  @Getter
  @Setter
  private Float potentialScore;

  @SerializedName("approval_status")
  @Column(name="approval_status")
  @Getter
  @Setter
  private Integer approvalStatus;

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
  private List<DocumentFile> documents;

  @Transient
  @Getter
  @Setter
  private List<Comment> comments;

  @ManyToOne(targetEntity = MasterCriteria.class)
  @JoinColumn(name="master_criteria_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private MasterCriteria criteria;

  public CriteriaScoring() {
  }

  public CriteriaScoring(Integer id, Integer masterCriteriaID, Integer projectAssessmentID) {
    this.id = id;
    this.masterCriteriaID = masterCriteriaID;
    this.projectAssessmentID = projectAssessmentID;
  }
}
