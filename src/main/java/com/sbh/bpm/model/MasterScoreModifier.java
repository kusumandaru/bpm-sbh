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
@Table(name = "master_score_modifiers")
public class MasterScoreModifier {
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

  @SerializedName("title")
  @Column(name="title")
  @Getter
  @Setter
  private String title;

  @SerializedName("description")
  @Column(name="description")
  @Getter
  @Setter
  private String description;

  @SerializedName("score_modifier")
  @Column(name="score_modifier")
  @Getter
  @Setter
  private Float scoreModifier;

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

  public MasterScoreModifier() {
  }

  public MasterScoreModifier(Integer id, String title, String description, Float scoreModifier) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.scoreModifier = scoreModifier;
  }
}
