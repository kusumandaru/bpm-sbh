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
@Table(name = "regencies")
public class City {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("province_id")
  @Column(name="province_id", length=2, nullable=false)
  @Getter
  @Setter
  private Integer provinceId;

  @Getter
  @Setter
  private String name;

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

  @ManyToOne(targetEntity = Province.class)
  @JoinColumn(name="province_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private Province province;

  public City() {
  }

  public City(Integer id, Integer provinceId, String name) {
    this.id = id;
    this.provinceId = provinceId;
    this.name = name;
  }
}
