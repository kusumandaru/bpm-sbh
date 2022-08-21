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
@Table(name = "project_document_categories")
public class ProjectDocumentCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("name")
  @Column(name="name")
  @Getter
  @Setter
  private String name;

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


  public ProjectDocumentCategory() {
  }

  public ProjectDocumentCategory(Integer id, String code, String name, String description, Boolean active, Date createdAt, String createdBy) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.description = description;
    this.active = active;
    this.createdAt = createdAt;
    this.createdBy = createdBy;


  }
}
