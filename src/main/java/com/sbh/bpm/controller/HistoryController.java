package com.sbh.bpm.controller;

import static org.mockito.ArgumentMatchers.contains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sbh.bpm.model.ActivityHistory;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.service.ITransactionCreationService;
import com.sbh.bpm.service.IUserService;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricDetail;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/history")
public class HistoryController extends GcsUtil {
  private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
  
  @Autowired
  private IUserService userService;

  @GET
  @Path(value = "/variables/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response VariableHistory(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    HistoryService historyService = processEngine.getHistoryService();

    List<HistoricVariableInstance> historicVariables = historyService.createHistoricVariableInstanceQuery()
                                                                     .processInstanceId(task.getProcessInstanceId()).list();

    String json = new Gson().toJson(historicVariables);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/process_instances/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ProcessInstanceHistory(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    HistoryService historyService = processEngine.getHistoryService();

    List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                                                                     .processInstanceId(task.getProcessInstanceId()).list();

    String json = new Gson().toJson(historicProcessInstances);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/activities/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ActivityHistory(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    HistoryService historyService = processEngine.getHistoryService();

    List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery()
                                                                     .processInstanceId(task.getProcessInstanceId()).list();

    List<String> activityTypes = new ArrayList<String>(Arrays.asList("userTask"));
    List<User> allUsers = userService.findAll();
    
    List<ActivityHistory> activityHistories = historicActivityInstances.stream()
                          .filter(history -> activityTypes.contains(history.getActivityType()))
                          .sorted((p1, p2)->p1.getStartTime().compareTo(p2.getStartTime()))
                          .map(history -> {
                            Optional<User> result = allUsers.stream().filter(u -> u.getId().equals(history.getAssignee())).findFirst();
                            User assignee = null;
                            if (result.isPresent()) {
                              assignee = result.get();
                            }
                            return ActivityHistory.CreateActivityHistoryFromHistoricActivityInstance(history, assignee);
                          })
                          .collect(Collectors.toList());
    String json = new Gson().toJson(activityHistories);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/details/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DetailHistory(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    HistoryService historyService = processEngine.getHistoryService();

    List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery()
                                                                     .processInstanceId(task.getProcessInstanceId()).list();

    String json = new Gson().toJson(historicDetails);
    return Response.ok(json).build();
  }

}