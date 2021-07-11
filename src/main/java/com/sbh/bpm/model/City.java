package com.sbh.bpm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "regencies")
public class City {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private String id;

  @Column(name="province_id", length=2)
  private String provinceId;

  private String name;

  public City() {
  }

  public City(String id, String provinceId, String name) {
    this.id = id;
    this.provinceId = provinceId;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setProvinceId(String provinceId) {
    this.provinceId = provinceId;
  }

  public String getProvinceId() {
    return provinceId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
