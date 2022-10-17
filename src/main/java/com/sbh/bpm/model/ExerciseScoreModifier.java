package com.sbh.bpm.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exercise_score_modifiers")
public class ExerciseScoreModifier {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_score_modifier_id")
  @Column(name="master_score_modifier_id")
  @Getter
  @Setter
  private Integer masterScoreModifierID;

  @SerializedName("project_assessment_id")
  @Column(name="project_assessment_id")
  @Getter
  @Setter
  private Integer projectAssessmentID;

  @SerializedName("exercise_assessment_id")
  @Column(name="exercise_assessment_id")
  @Getter
  @Setter
  private Integer exerciseAssessmentID;

  @SerializedName("score_modifier")
  @Column(name="score_modifier")
  @Getter
  @Setter
  private Float scoreModifier;

  @SerializedName("enabled")
  @Column(name="enabled")
  @Getter
  @Setter
  private Boolean enabled;

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

  @SerializedName("master_score_modifier")
  @ManyToOne(targetEntity = MasterScoreModifier.class)
  @JoinColumn(name="master_score_modifier_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private MasterScoreModifier masterScoreModifier;

  public ExerciseScoreModifier() {
  }

  public ExerciseScoreModifier(Integer id, Boolean enabled, Float scoreModifier) {
    this.id = id;
    this.enabled = enabled;
    this.scoreModifier = scoreModifier;
  }
}
