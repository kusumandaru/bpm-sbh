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
@Table(name = "master_certification_types")
public class MasterCertificationType {
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

  @SerializedName("certification_code")
  @Column(name="certification_code")
  @Getter
  @Setter
  private String certificationCode;

  @SerializedName("certification_name")
  @Column(name="certification_name")
  @Getter
  @Setter
  private String certificationName;

  @SerializedName("design_recognition")
  @Column(name="design_recognition")
  @Getter
  @Setter
  private Boolean designRecognition;

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


  public MasterCertificationType() {
  }

  public MasterCertificationType(Integer id, Integer masterVendorID, String certificationCode, String certificationName) {
    this.id = id;
    this.masterVendorID = masterVendorID;
    this.certificationCode = certificationCode;
    this.certificationName = certificationName;
  }
}
