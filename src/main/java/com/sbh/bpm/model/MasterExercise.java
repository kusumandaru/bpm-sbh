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
@Table(name = "master_exercises")
public class MasterExercise {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_evaluation_id")
  @Column(name="master_evaluation_id")
  @Getter
  @Setter
  private Integer masterEvaluationID;

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

  @SerializedName("name")
  @Column(name="name")
  @Getter
  @Setter
  private String name;

  @SerializedName("max_score")
  @Column(name="max_score")
  @Getter
  @Setter
  private Integer maxScore;

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

  // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)//
  // private List<MasterEvaluation> evaluations = new ArrayList<>();
    
  public MasterExercise() {
  }

  public MasterExercise(Integer id, String code, String name, String exerciseType, Integer maxScore) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.exerciseType = exerciseType;
    this.maxScore = maxScore;
  }
}
