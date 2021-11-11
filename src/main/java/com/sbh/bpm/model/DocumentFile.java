package com.sbh.bpm.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "document_files")
public class DocumentFile {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_document_id")
  @Column(name="master_document_id")
  @Getter
  @Setter
  private Integer masterDocumentID;

  @SerializedName("criteria_scoring_id")
  @Column(name="criteria_scoring_id")
  @Getter
  @Setter
  private Integer criteriaScoringID;

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

  @ManyToOne(targetEntity = MasterDocument.class)
  @JoinColumn(name="master_document_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private MasterDocument document;

  @Transient
  @Getter
  @Setter
  private List<Attachment> attachments;

  public DocumentFile() {
  }

  public DocumentFile(Integer id, Integer masterDocumentID, Integer criteriaScoringID) {
    this.id = id;
    this.masterDocumentID = masterDocumentID;
    this.criteriaScoringID = criteriaScoringID;
  }
}
