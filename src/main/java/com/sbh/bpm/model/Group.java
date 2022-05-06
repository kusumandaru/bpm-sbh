package com.sbh.bpm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ACT_ID_GROUP")
public class Group {
  @Id
  @SerializedName("id")
  @Column(name="ID_", nullable=false)
  @Getter
  @Setter
  private String id;

  @Getter
  @Setter
  @SerializedName("rev")
  @Column(name="REV_")
  private Integer rev;

  @Getter
  @Setter
  @SerializedName("name")
  @Column(name="NAME_")
  private String name;

  @Getter
  @Setter
  @SerializedName("type")
  @Column(name="TYPE_")
  private String type;

  public Group() {
  }

  public Group(String id, String name, String type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }
}
