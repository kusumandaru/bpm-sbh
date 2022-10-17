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
@Table(name = "master_documents")
public class  MasterDocument {
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

  @SerializedName("name")
  @Column(name="name")
  @Getter
  @Setter
  private String name;

  @SerializedName("criteria_code")
  @Column(name="criteria_code")
  @Getter
  @Setter
  private String criteriaCode;

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

  public MasterDocument() {
  }

  public MasterDocument(Integer id, Integer masterCriteriaID, String name) {
    this.id = id;
    this.masterCriteriaID = masterCriteriaID;
    this.name = name;
  }
}
