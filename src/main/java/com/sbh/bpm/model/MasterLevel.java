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
@Table(name = "master_levels")
public class MasterLevel {
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

  @SerializedName("name")
  @Column(name="name")
  @Getter
  @Setter
  private String name;

  @SerializedName("minimum_score")
  @Column(name="minimum_score")
  @Getter
  @Setter
  private Float minimumScore;

  @SerializedName("percentage")
  @Column(name="percentage")
  @Getter
  @Setter
  private Float percentage;

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

  public MasterLevel() {
  }

  public MasterLevel(Integer id,  String name, Float minimumScore, Float percentage) {
    this.id = id;
    this.name = name;
    this.minimumScore = minimumScore;
    this.percentage = percentage;
  }
}
