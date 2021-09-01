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
@Table(name = "sequence_numbers")
public class SequenceNumber {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @Getter
  @Setter
  private String code;

  @Getter
  @Setter
  private Integer year;

  @Getter
  @Setter
  private Integer sequence;

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

  public SequenceNumber() {
  }

  public SequenceNumber(Integer id, Integer year, Integer sequence, String code) {
    this.id = id;
    this.year = year;
    this.sequence = sequence;
    this.code = code;
  }
}
