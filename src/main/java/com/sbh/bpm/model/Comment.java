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
@Table(name = "comments")
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("criteria_scoring_id")
  @Column(name="criteria_scoring_id")
  @Getter
  @Setter
  private Integer criteriaScoringID;

  @SerializedName("user_id")
  @Column(name="user_id")
  @Getter
  @Setter
  private String userID;

  @SerializedName("role")
  @Column(name="role")
  @Getter
  @Setter
  private String role;

  @SerializedName("comment")
  @Column(name="comment")
  @Getter
  @Setter
  private String comment;

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

  @ManyToOne(targetEntity = User.class)
  @JoinColumn(name="user_id",referencedColumnName="ID_",insertable=false,updatable=false)
  @Getter
  @Setter
  private User user;

  public Comment() {
  }

  public Comment(Integer id, Integer criteriaScoringID) {
    this.id = id;
    this.criteriaScoringID = criteriaScoringID;
  }
}
