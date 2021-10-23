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
@Table(name = "master_criteria_blockers")
public class MasterCriteriaBlocker {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("master_criteria_id")
  @Column(name="master_criteria_id")
  @Getter
  @Setter
  private Integer masterCriteriaID;

  @SerializedName("blocker_id")
  @Column(name="blocker_id")
  @Getter
  @Setter
  private Integer blockerID;

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

  // @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)//
  // private List<MasterCriteriaBlocker> criterias = new ArrayList<>();

  @ManyToOne(targetEntity = MasterCriteria.class)
  @JoinColumn(name="blocker_id",referencedColumnName="id",insertable=false,updatable=false)
  @Getter
  @Setter
  private MasterCriteria blocker;
    
  public MasterCriteriaBlocker() {
  }

  public MasterCriteriaBlocker(Integer id, Integer masterCriteriaID, Integer blockerID) {
    this.id = id;
    this.masterCriteriaID = masterCriteriaID;
    this.blockerID = blockerID;
  }
}
