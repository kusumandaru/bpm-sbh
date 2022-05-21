package com.sbh.bpm.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;

import com.google.gson.annotations.SerializedName;

import org.camunda.bpm.engine.task.Task;

import lombok.Getter;
import lombok.Setter;

public class SbhTask {
  @Getter @Setter
  private String id;

  @Getter @Setter
  private Integer revision;

  @Getter @Setter
  private String assignee;

  @Getter @Setter
  private String owner;

  @Getter @Setter
  private String name;

  @Getter @Setter
  private Boolean read;

  @SerializedName("created_at")
  @Getter @Setter
  private Date createTime;

  @SerializedName("suspension_state")
  @Getter @Setter
  private Integer suspensionState;

  @SerializedName("lifecycle_state")
  @Getter @Setter
  private String lifecycleState;

  @SerializedName("execution_id")
  @Getter @Setter
  private String executionId;

  @SerializedName("process_instance_id")
  @Getter @Setter
  private String processInstanceId;

  @SerializedName("process_definition_id")
  @Getter @Setter
  private String processDefinitionId;

  @SerializedName("task_definition_key")
  @Getter @Setter
  private String taskDefinitionKey;

  @SerializedName("tenant_id")
  @Getter @Setter
  private String tenantId;

  @SerializedName("tenant_name")
  @Getter @Setter
  private String tenantName;

  @SerializedName("certification_type")
  @Getter @Setter
  @Column(name="certification_type")
  private String certificationType;

  @Getter @Setter
  @SerializedName("building_type")
  @Column(name="building_type")
  private String buildingType;

  @SerializedName("building_type_name")
  @Getter @Setter
  @Column(name="building_type_name")
  private String buildingTypeName;

  @Getter @Setter
  @SerializedName("building_name")
  @Column(name="building_name")
  private String buildingName;

  @Getter @Setter
  @Column(name="status")
  private String status;

  @Getter @Setter
  @Column(name="assigned")
  private Boolean assigned;

  public static SbhTask CreateFromTask(Task cTask) {
    SbhTask sbhTask = new SbhTask();
    sbhTask.id = cTask.getId();
    sbhTask.assignee = cTask.getAssignee();
    sbhTask.owner = cTask.getOwner();
    sbhTask.name = cTask.getName();
    sbhTask.processDefinitionId = cTask.getProcessDefinitionId();
    sbhTask.executionId = cTask.getExecutionId();
    sbhTask.processInstanceId = cTask.getProcessInstanceId();
    sbhTask.createTime = cTask.getCreateTime();
    sbhTask.taskDefinitionKey = cTask.getTaskDefinitionKey();

    return sbhTask;
  }

  public static SbhTask AssignTaskVariables(SbhTask sbhTask, Map<String, Object> variableMap) {
    sbhTask.buildingName =  String.valueOf(variableMap.get("building_name"));
    sbhTask.buildingType =  String.valueOf(variableMap.get("building_type"));
    sbhTask.certificationType =  String.valueOf(variableMap.get("certification_type"));
    sbhTask.tenantId = String.valueOf(variableMap.get("tenant"));
    if (variableMap.get("read") != null) {
      sbhTask.read = ((Boolean) variableMap.get("read")).booleanValue();
    } else {
      sbhTask.read = false; 
    }
    switch(String.valueOf(variableMap.get("approved"))) {
      case "true":
        sbhTask.status = "approved";
        break;
      case "false":
        sbhTask.status = "rejected";
        break;
      default:
      sbhTask.status = "pending";
    }

    return sbhTask;
  }

}