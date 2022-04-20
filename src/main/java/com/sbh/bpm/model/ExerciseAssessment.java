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
@Table(name = "exercise_assessments")
public class ExerciseAssessment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_exercise_id")
  @Column(name="master_exercise_id")
  @Getter
  @Setter
  private Integer masterExerciseID;

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

  @SerializedName("approved_score")
  @Column(name="approved_score")
  @Getter
  @Setter
  private Float approvedScore;

  @SerializedName("submitted_score")
  @Column(name="submitted_score")
  @Getter
  @Setter
  private Float submittedScore;

  @SerializedName("score_modifier")
  @Column(name="score_modifier")
  @Getter
  @Setter
  private Float scoreModifier;

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

  @ManyToOne(targetEntity = MasterExercise.class)
  @JoinColumn(name="master_exercise_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private MasterExercise exercise;

  @Transient
  @Getter
  @Setter
  private List<CriteriaScoring> criterias;

  public ExerciseAssessment() {
  }

  public ExerciseAssessment(Integer id, Integer masterExerciseID, Integer projectAssessmentID) {
    this.id = id;
    this.masterExerciseID = masterExerciseID;
    this.projectAssessmentID = projectAssessmentID;
  }
}
