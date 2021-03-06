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
@Table(name = "building_types")
public class BuildingType {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @Getter
  @Setter
  private String code;

  @SerializedName("name_id")
  @Column(name="name_id")
  @Getter
  @Setter
  private String nameId;

  @SerializedName("name_en")
  @Column(name="name_en")
  @Getter
  @Setter
  private String nameEn;

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

  public BuildingType() {
  }

  public BuildingType(Integer id, String code, String nameId, String nameEn) {
    this.id = id;
    this.code = code;
    this.nameId = nameId;
    this.nameEn = nameEn;
  }
}
