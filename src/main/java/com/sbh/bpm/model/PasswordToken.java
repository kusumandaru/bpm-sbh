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
@Table(name = "password_tokens")
public class PasswordToken {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("user_id")
  @Column(name="user_id")
  @Getter
  @Setter
  private String userId;

  @SerializedName("token")
  @Column(name="token")
  @Getter
  @Setter
  private String token;

  @SerializedName("expire_date")
  @Column(name="expire_date")
  @Getter
  @Setter
  private Date expireDate;

  @ManyToOne(targetEntity = User.class)
  @JoinColumn(name="user_id",referencedColumnName="ID_",insertable=false,updatable=false)
  @Getter
  @Setter
  private User user;

  public PasswordToken() {
  }

  public PasswordToken(String userId, String token, Date expireDate) {
    this.userId = userId;
    this.token = token;
    this.expireDate = expireDate;
  }
}
