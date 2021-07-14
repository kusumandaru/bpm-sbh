package com.sbh.bpm.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provinces")
public class Province {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @Getter
  @Setter
  private String name;

  @Column(name="created_at")
  @Getter
  @Setter
  private Date createdAt;

  @Column(name="updated_at")
  @Getter
  @Setter
  private Date updatedAt;

  public Province() {
  }

  public Province(Integer id, String name) {
    this.id = id;
    this.name = name;
  }
}
