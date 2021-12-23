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
@Table(name = "project_attachments")
public class ProjectAttachment {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("process_instance_id")
  @Column(name="process_instance_id")
  @Getter
  @Setter
  private String processInstanceID;

  @SerializedName("file_type")
  @Column(name="file_type")
  @Getter
  @Setter
  private String fileType;

  @SerializedName("filename")
  @Column(name="filename")
  @Getter
  @Setter
  private String filename;

  @SerializedName("blob_url")
  @Column(name="blob_url")
  @Getter
  @Setter
  private String blobUrl;

  @SerializedName("link")
  @Column(name="link")
  @Getter
  @Setter
  private String link;

  @SerializedName("version")
  @Column(name="version")
  @Getter
  @Setter
  private Integer version;

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

  public ProjectAttachment() {
  }

  public ProjectAttachment(Integer id, String processInstanceID) {
    this.id = id;
    this.processInstanceID = processInstanceID;
  }
}
