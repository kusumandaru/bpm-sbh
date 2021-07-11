package com.sbh.bpm.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;

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
  private Date createTime;
  @Getter @Setter
  private Integer suspensionState;
  @Getter @Setter
  private String lifecycleState;
  @Getter @Setter
  private String executionId;
  @Getter @Setter
  private String processInstanceId;
  @Getter @Setter
  private String processDefinitionId;
  @Getter @Setter
  private String taskDefinitionKey;
  @Getter @Setter
  private String tenantId;
  @Getter @Setter
  @Column(name="certification_type")
  private String certificationType;
  @Getter @Setter
  @Column(name="building_type")
  private String buildingType;
  @Getter @Setter
  @Column(name="building_name")
  private String buildingName;

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
    sbhTask.tenantId = cTask.getTenantId();

    return sbhTask;
  }

  public static SbhTask AssignTaskVariables(SbhTask sbhTask, Map<String, Object> variableMap) {
    sbhTask.buildingName =  String.valueOf(variableMap.get("building_name"));
    sbhTask.buildingType =  String.valueOf(variableMap.get("building_type"));
    sbhTask.certificationType =  String.valueOf(variableMap.get("certification_type"));

    return sbhTask;
  }

}