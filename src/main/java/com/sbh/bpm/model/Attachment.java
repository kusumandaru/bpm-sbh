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
@Table(name = "attachments")
public class Attachment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("document_file_id")
  @Column(name="document_file_id")
  @Getter
  @Setter
  private Integer documentFileID;

  @SerializedName("filename")
  @Column(name="filename")
  @Getter
  @Setter
  private String filename;

  @SerializedName("link")
  @Column(name="link")
  @Getter
  @Setter
  private String link;

  @SerializedName("uploader_id")
  @Column(name="uploader_id")
  @Getter
  @Setter
  private String uploaderID;

  @SerializedName("role")
  @Column(name="role")
  @Getter
  @Setter
  private String role;

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

  @SerializedName("submitted_at")
  @Column(name="submitted_at")
  @Getter
  @Setter
  private Date submittedAt;

  @SerializedName("created_by")
  @Column(name="created_by")
  @Getter
  @Setter
  private String createdBy;

  @ManyToOne(targetEntity = User.class)
  @JoinColumn(name="uploader_id",referencedColumnName="ID_",insertable=false,updatable=false)
  @Getter
  @Setter
  private User user;

  public Attachment() {
  }

  public Attachment(Integer id, Integer documentFileID) {
    this.id = id;
    this.documentFileID = documentFileID;
  }
}
