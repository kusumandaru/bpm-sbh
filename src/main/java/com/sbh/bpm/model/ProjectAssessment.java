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
import javax.validation.constraints.Pattern;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_assessments")
public class ProjectAssessment {
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

  @SerializedName("process_instance_id")
  @Column(name="process_instance_id")
  @Getter
  @Setter
  private String processInstanceID;

  @SerializedName("project_name")
  @Column(name="project_name")
  @Getter
  @Setter
  private String projectName;

  @SerializedName("submitted_score")
  @Column(name="submitted_score")
  @Getter
  @Setter
  private Float submittedScore;

  @SerializedName("approved_score")
  @Column(name="approved_score")
  @Getter
  @Setter
  private Float approvedScore;

  @SerializedName("potential_score")
  @Column(name="potential_score")
  @Getter
  @Setter
  private Float potentialScore;

  @SerializedName("level_id")
  @Column(name="level_id")
  @Getter
  @Setter
  private Integer levelID;

  @SerializedName("proposed_level_id")
  @Column(name="proposed_level_id")
  @Getter
  @Setter
  private Integer proposedLevelID;

  @SerializedName("assessment_attachment")
  @Column(name="assessment_attachment")
  @Getter
  @Setter
  private String assessmentAttachment;

  @SerializedName("assessment_type")
  @Column(name="assessment_type")
  @Pattern(regexp = "DR|FA", flags = Pattern.Flag.CASE_INSENSITIVE)
  @Getter
  @Setter
  private String assessmentType;

  @SerializedName("approval_note")
  @Column(name="approval_note", columnDefinition = "TEXT")
  @Getter
  @Setter
  private String approvalNote;

  @SerializedName("approval_status")
  @Column(name="approval_status")
  @Getter
  @Setter
  @Pattern(regexp = "approved|referenced|rejected", flags = Pattern.Flag.CASE_INSENSITIVE)
  private String approvalStatus;

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

  @SerializedName("proposed_level")
  @ManyToOne(targetEntity = MasterLevel.class)
  @JoinColumn(name="proposed_level_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private MasterLevel proposedLevel;

  @Transient
  @Getter
  @Setter
  private Integer targetScore;

  @Transient
  @Getter
  @Setter
  private List<MasterEvaluation> masterEvaluations;

  public ProjectAssessment() {
  }

  public ProjectAssessment(Integer id, Integer masterTemplateID, String processInstanceID) {
    this.id = id;
    this.masterTemplateID = masterTemplateID;
    this.processInstanceID = processInstanceID;
  }
}
