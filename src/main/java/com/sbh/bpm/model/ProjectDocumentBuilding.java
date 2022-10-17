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
@Table(name = "project_document_buildings")
public class ProjectDocumentBuilding {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_certification_type_id")
  @Column(name="master_certification_type_id")
  @Getter
  @Setter
  private Integer masterCertificationTypeID;

  @SerializedName("project_document_category_id")
  @Column(name="project_document_category_id")
  @Getter
  @Setter
  private Integer projectDocumentCategoryID;

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

  @SerializedName("placeholder")
  @Column(name="placeholder")
  @Getter
  @Setter
  private String placeholder;

  @SerializedName("object_type")
  @Column(name="object_type")
  @Getter
  @Setter
  private String objectType;

  @SerializedName("mandatory")
  @Column(name="mandatory")
  @Getter
  @Setter
  private Boolean mandatory;

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

  @ManyToOne(targetEntity = ProjectDocumentCategory.class)
  @JoinColumn(name="project_document_category_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private ProjectDocumentCategory category;

  public ProjectDocumentBuilding() {
  }

  public ProjectDocumentBuilding(String code, String name) {
    this.code = code;
    this.name = name;
    this.active = true;
    this.mandatory = true;
  }
}
