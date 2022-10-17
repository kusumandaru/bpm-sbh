package com.sbh.bpm.model;

import java.util.Date;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.Setter;

public class ActivityHistory {
  @Id
  @Getter
  @Setter
  private String id;

  @Getter
  @Setter
  private String parentActivityInstanceId;

  @Getter
  @Setter
  private String activityId;

  @Getter
  @Setter
  private String activityName;

  @Getter
  @Setter
  private String activityType;

  @Getter
  @Setter
  private String processDefinitionKey;

  @Getter
  @Setter
  private String processDefinitionId;

  @Getter
  @Setter
  private String rootProcessInstanceId;

  @Getter
  @Setter
  private String processInstanceId;

  @Getter
  @Setter
  private String executionId;

  @Getter
  @Setter
  private String taskId;

  @Getter
  @Setter
  private String calledProcessInstanceId;

  @Getter
  @Setter
  private String calledCaseInstanceId;

  @Getter
  @Setter
  private String assignee;

  @Getter
  @Setter
  private Date startTime;

  @Getter
  @Setter
  private Date endTime;

  @Getter
  @Setter
  private Long durationInMillis;

  @Getter
  @Setter
  boolean isCompleteScope;

  @Getter
  @Setter
  boolean isCanceled;

  @Getter
  @Setter
  private String tenantId;

  @Getter
  @Setter
  private Date removalTime;

  @Getter
  @Setter
  private User user;

  public static  ActivityHistory CreateActivityHistoryFromHistoricActivityInstance(HistoricActivityInstance activity, User user) {
    ActivityHistory history = new ActivityHistory();
    history.id = activity.getId();
    history.parentActivityInstanceId = activity.getParentActivityInstanceId();
    history.activityId = activity.getActivityId();
    history.activityName = activity.getActivityName();
    history.activityType = activity.getActivityType();
    history.processDefinitionKey = activity.getProcessDefinitionKey();
    history.processDefinitionId = activity.getProcessDefinitionId();
    history.rootProcessInstanceId = activity.getRootProcessInstanceId();
    history.processInstanceId = activity.getProcessInstanceId();
    history.executionId = activity.getExecutionId();
    history.taskId = activity.getTaskId();
    history.calledProcessInstanceId = activity.getCalledCaseInstanceId();
    history.calledCaseInstanceId = activity.getCalledCaseInstanceId();
    history.assignee = activity.getAssignee();
    history.startTime = activity.getStartTime();
    history.endTime = activity.getEndTime();
    history.durationInMillis = activity.getDurationInMillis();
    history.isCompleteScope = activity.isCompleteScope();
    history.isCanceled = activity.isCanceled();
    history.tenantId = activity.getTenantId();
    history.removalTime = activity.getRemovalTime();
    history.user = user;

    return history;
  }

}
