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
@Table(name = "master_templates")
public class MasterTemplate {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_vendor_id")
  @Column(name="master_vendor_id")
  @Getter
  @Setter
  private Integer masterVendorID;

  @SerializedName("project_type")
  @Column(name="project_type")
  @Getter
  @Setter
  private String projectType;

  @SerializedName("project_version")
  @Column(name="project_version")
  @Getter
  @Setter
  private String projectVersion;

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

  // @ManyToOne(targetEntity = MasterVendor.class)
  // @JoinColumn(name="master_vendor_id",referencedColumnName="id",insertable=false,updatable=false)
  // @Getter
  // @Setter
  // private MasterVendor vendor;

  public MasterTemplate() {
  }

  public MasterTemplate(Integer id, String projectType, String projectVersion) {
    this.id = id;
    this.projectType = projectType;
    this.projectVersion = projectVersion;
  }
}
