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
@Table(name = "master_admins")
public class MasterAdmin {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Getter
  @Setter
  private Integer id;

  @SerializedName("manager_name")
  @Column(name="manager_name")
  @Getter
  @Setter
  private String managerName;

  @SerializedName("manager_signature")
  @Column(name="manager_signature")
  @Getter
  @Setter
  private String managerSignature;

  @SerializedName("registration_letter")
  @Column(name="registration_letter")
  @Getter
  @Setter
  private String registrationLetter;

  @SerializedName("first_attachment")
  @Column(name="first_attachment")
  @Getter
  @Setter
  private String firstAttachment;

  @SerializedName("second_attachment")
  @Column(name="second_attachment")
  @Getter
  @Setter
  private String secondAttachment;

  @SerializedName("third_attachment")
  @Column(name="third_attachment")
  @Getter
  @Setter
  private String thirdAttachment;

  @SerializedName("scoring_form")
  @Column(name="scoring_form")
  @Getter
  @Setter
  private String scoringForm;

  @SerializedName("dr_template_id")
  @Column(name="dr_template_id")
  @Getter
  @Setter
  private Integer drTemplateID;

  @SerializedName("fa_template_id")
  @Column(name="fa_template_id")
  @Getter
  @Setter
  private Integer faTemplateID;

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

  public MasterAdmin() {
  }

  public MasterAdmin(Integer id, String managerName, String managerSignature, 
        String registrationLetter, String firstAttachment, String secondAttachment, 
        String thirdAttachment, Integer drTemplateID, Integer faTemplateID) {
    this.id = id;
    this.managerName = managerName;
    this.managerSignature = managerSignature;
    this.registrationLetter = registrationLetter;
    this.firstAttachment = firstAttachment;
    this.secondAttachment = secondAttachment;
    this.thirdAttachment = thirdAttachment;
    this.drTemplateID = drTemplateID;
    this.faTemplateID = faTemplateID;
  }
}
