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
@Table(name = "master_criterias")
public class MasterCriteria {
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

  @SerializedName("exercise_type")
  @Column(name="exercise_type")
  @Getter
  @Setter
  private String exerciseType;

  @SerializedName("code")
  @Column(name="code")
  @Getter
  @Setter
  private String code;

  @SerializedName("description")
  @Column(name="description")
  @Getter
  @Setter
  private String description;

  @SerializedName("score")
  @Column(name="score")
  @Getter
  @Setter
  private Float score;

  @SerializedName("additional_notes")
  @Column(name="additional_notes")
  @Getter
  @Setter
  private String additionalNotes;

  @SerializedName("not_available")
  @Column(name="not_available")
  @Getter
  @Setter
  private Boolean notAvailable;

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

  public MasterCriteria() {
  }

  public MasterCriteria(Integer id, String code, String description, String exerciseType, Float score) {
    this.id = id;
    this.code = code;
    this.description = description;
    this.exerciseType = exerciseType; 
    this.score = score;
  }
}
