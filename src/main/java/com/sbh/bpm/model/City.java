package com.sbh.bpm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "regencies")
public class City {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @Column(name="province_id", length=2)
  @Getter
  @Setter
  private Integer provinceId;

  @Getter
  @Setter
  private String name;

  public City() {
  }

  public City(Integer id, Integer provinceId, String name) {
    this.id = id;
    this.provinceId = provinceId;
    this.name = name;
  }
}
