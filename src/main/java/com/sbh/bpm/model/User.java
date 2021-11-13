package com.sbh.bpm.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`ACT_ID_USER`")
public class User {
  @Id
  @Column(name="ID_")
  @Getter
  @Setter
  private String id;

  @Getter
  @Setter
  @Column(name="REV_")
  private Integer rev;

  @Getter
  @Setter
  @SerializedName("first_name")
  @Column(name="FIRST_")
  private String firstName;

  @Getter
  @Setter
  @SerializedName("last_name")
  @Column(name="LAST_")
  private String lastName;

  @Getter
  @Setter
  @SerializedName("email")
  @Column(name="EMAIL_")
  private String email;

  public User() {
  }

  public User(String id, String firstName, String lastName, String email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }
}
