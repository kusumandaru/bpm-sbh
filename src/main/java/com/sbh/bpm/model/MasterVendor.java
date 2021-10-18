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
@Table(name = "master_vendors")
public class MasterVendor {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("vendor_code")
  @Column(name="vendor_code")
  @Getter
  @Setter
  private String vendorCode;

  @SerializedName("vendor_name")
  @Column(name="vendor_name")
  @Getter
  @Setter
  private String vendorName;

  @SerializedName("description")
  @Column(name="description")
  @Getter
  @Setter
  private String description;

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
  // private List<MasterTemplate> templates = new ArrayList<>();
    
  public MasterVendor() {
  }

  public MasterVendor(Integer id, String vendorCode, String vendorName, String description) {
    this.id = id;
    this.vendorCode = vendorCode;
    this.vendorName = vendorName;
    this.description = description;
  }
}
