package com.sbh.bpm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.gson.Gson;
import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.PaginationRequest;
import com.sbh.bpm.model.PaginationResult;
import com.sbh.bpm.model.ProjectUser;
import com.sbh.bpm.model.ProjectVerificator;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.model.SbhTask;
import com.sbh.bpm.model.Tenant;
import com.sbh.bpm.model.User;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.service.IBuildingTypeService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IProjectUserService;
import com.sbh.bpm.service.IProjectVerificatorService;
import com.sbh.bpm.service.IProvinceService;
import com.sbh.bpm.service.ITenantService;
import com.sbh.bpm.service.ITransactionCreationService;
import com.sbh.bpm.service.IUserService;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.AuthorizationException;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.rest.dto.repository.ActivityStatisticsResultDto;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class TaskController {
  private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
  private final String[] adminTasks = {"check-registration-project", "check-document-building", "agreement", "check-first-payment", "workshop", "check-second-payment", "check-third-payment", "design-recognition-evaluation-assessment", "design-recognition-trial", "design-recognition-letter", "check-third-payment-fa"};
  private final String[] clientTasks = {"fill-registration-project", "fill-document-building", "first-payment", "second-payment", "design-recognition-submission", "third-payment", "design-recognition-trial-revision", "final-assessment-submission", "third-payment-fa", "on-site-revision-submission", "final-assessment-trial-revision", "final-assessment-letter"};
  private final String[] verificatorTasks = {"design-recognition-review", "design-recognition-revision-review", "final-assessment-review", "on-site-verification", "final-assessment-evaluation-assessment", "final-assessment-revision-review"};

  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

  @Autowired
  private IBuildingTypeService buildingTypeService;

  @Autowired
  private IMailerService mailerService;

  @Autowired
  private ITransactionCreationService transactionCreationService;

  @Autowired
  private IUserService userService;

  @Autowired
  private ITenantService tenantService;

  @Autowired
  private IProjectUserService projectUserService;

  @Autowired
  private IProjectVerificatorService projectVerificatorService;

  @Context
  UriInfo uriInfo;

  @GET
  @Path(value = "/tasks/admin")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AdminTasks(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().taskDefinitionKeyIn("check-registration-project", "check-document-building").active().orderByTaskCreateTime().desc().list();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks/admin/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AdminTasksByUserId(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId
    ) { 
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().active().orderByTaskCreateTime().desc().list();
    List<Tenant> tenants = tenantService.findAll();
    List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserId(userId);

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      String tenantId = sbhTask.getTenantId();
      Tenant selectedTenant = tenants.stream().filter(tnt -> tnt.getId().equals(tenantId)).findFirst().get();
      sbhTask.setTenantName(selectedTenant.getName());
      final SbhTask innerTask = sbhTask;
      Boolean assigned = projectVerificators.stream().anyMatch(project -> project.getProcessInstanceID().equals(innerTask.getProcessInstanceId()));
      sbhTask.setAssigned(assigned);
      
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks/client")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ClientTasks(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().taskDefinitionKeyIn("fill-registration-project", "fill-document-building").active().orderByTaskCreateTime().desc().list();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks/statistics")
  @Produces(MediaType.APPLICATION_JSON)
  public Response StatisticTasks(@HeaderParam("Authorization") String authorization) { 
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }   

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.processVariableValueEquals("tenant", user.getTenant().getId());
    taskQuery = taskQuery.active().orderByTaskId().asc();
    List<Task> tasks = taskQuery.list();

    Map<String, Integer> map = new HashMap<String, Integer>(); 
    tasks.stream().forEach(t -> {
      if (!map.containsKey(t.getTaskDefinitionKey())) {
        map.put(t.getTaskDefinitionKey(), 0);
      }
        
      map.put(t.getTaskDefinitionKey(), map.get(t.getTaskDefinitionKey()) + 1);
    });

    List<ActivityStatisticsResultDto> dtoList =new ArrayList<ActivityStatisticsResultDto>();
    map.forEach((k,v) -> {
      ActivityStatisticsResultDto dto = new ActivityStatisticsResultDto();
      dto.setId(k);
      dto.setInstances(v);
      dtoList.add(dto);
    });
    String json = new Gson().toJson(dtoList);
    return Response.ok(json).build();
  } 

  @GET
  @Path(value = "/tasks")
  @Produces(MediaType.APPLICATION_JSON)
  public Response Tasks(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    List<Task> tasks = taskService.createTaskQuery().active().orderByTaskCreateTime().desc().list();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/grouped_task_by_tenant")
  @Produces(MediaType.APPLICATION_JSON)
  public Response TaskCountGroupByTenant(@HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Map<String, Long> dict = new TreeMap<>();
    List<Tenant> tenantList = tenantService.findAll();

    tenantList.stream().forEach(t -> {
      TaskQuery taskQuery = taskService.createTaskQuery();
      taskQuery = taskQuery.processVariableValueEquals("tenant", t.getId());
      dict.put(t.getName(), taskQuery.count());
    });

    String json = new Gson().toJson(dict);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/grouped_task_by_verificator")
  @Produces(MediaType.APPLICATION_JSON)
  public Response TaskCountGroupByVerificator(@HeaderParam("Authorization") String authorization) { 
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Map<String, Long> dict = new TreeMap<>();
    List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserId(user.getId());
    String[] processInstanceIds = projectVerificators.stream().map(pv -> pv.getProcessInstanceID()).toArray(String[]::new);
    List<Tenant> tenantList = tenantService.findAll();

    tenantList.stream().forEach(t -> {
      TaskQuery taskQuery = taskService.createTaskQuery();
      taskQuery = taskQuery.processVariableValueEquals("tenant", t.getId());
      taskQuery = taskQuery.processInstanceIdIn(processInstanceIds);
      dict.put(t.getName(), taskQuery.count());
    });

    dict.values().removeIf(f -> f == 0f);

    String json = new Gson().toJson(dict);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/grouped_pending_task_by_verificator")
  @Produces(MediaType.APPLICATION_JSON)
  public Response PendingTaskCountGroupByVerificator(@HeaderParam("Authorization") String authorization) { 
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Map<String, Long> dict = new TreeMap<>();
    List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserId(user.getId());
    String[] processInstanceIds = projectVerificators.stream().map(pv -> pv.getProcessInstanceID()).toArray(String[]::new);
    List<Tenant> tenantList = tenantService.findAll();

    tenantList.stream().forEach(t -> {
      TaskQuery taskQuery = taskService.createTaskQuery();
      taskQuery = taskQuery.processVariableValueEquals("tenant", t.getId());
      taskQuery = taskQuery.taskDefinitionKeyIn(verificatorTasks);
      taskQuery = taskQuery.processInstanceIdIn(processInstanceIds);
      dict.put(t.getName(), taskQuery.count());
    });

    dict.values().removeIf(f -> f == 0f);

    String json = new Gson().toJson(dict);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks/tenant/{user_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response TenantTasks(
    @HeaderParam("Authorization") String authorization,
    @PathParam("user_id") String userId
    ) { 
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.processVariableValueEquals("tenant", user.getTenant().getId());
    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();
    List<Task> tasks = taskQuery.list();

    List<ProjectUser> projectUsers = projectUserService.findByUserId(userId);
    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      final SbhTask innerTask = sbhTask;
      Boolean assigned = projectUsers.stream().anyMatch(project -> project.getProcessInstanceID().equals(innerTask.getProcessInstanceId()));
      sbhTask.setAssigned(assigned);
      
      sbhTasks.add(sbhTask);
    }
    String json = new Gson().toJson(sbhTasks);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/tasks/admin/pagi")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response AdminPaginationTasks(@HeaderParam("Authorization") String authorization,
                                  PaginationRequest pagiRequest) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.or();
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getAssignee())) {
      taskQuery = taskQuery.taskAssigneeLike("%"+pagiRequest.getFilter().getAssignee()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingTypeName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_type", pagiRequest.getFilter().getBuildingTypeName());
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_name", "%"+pagiRequest.getFilter().getBuildingName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getName())) {
      taskQuery = taskQuery.taskNameLike("%"+pagiRequest.getFilter().getName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getCertificationType())) {
      taskQuery = taskQuery.processVariableValueLike("certification_type", "%"+pagiRequest.getFilter().getCertificationType()+"%");
    }
    taskQuery = taskQuery.endOr();

    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();

    List<Task> tasks = taskQuery.listPage(pagiRequest.getPage(), pagiRequest.getSize());
    Long taskSize = taskQuery.count();

    List<Tenant> tenants = tenantService.findAll();
    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      String tenantId = sbhTask.getTenantId();
      Tenant selectedTenant = tenants.stream().filter(tnt -> tnt.getId().equals(tenantId)).findFirst().get();
      sbhTask.setTenantName(selectedTenant.getName());
      sbhTask.setAssigned(true);

      sbhTasks.add(sbhTask);
    }

    PaginationResult result = new PaginationResult(sbhTasks, taskSize);
    String json = new Gson().toJson(result);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/tasks/verificator/pagi")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response VerificatorPaginationTasks(@HeaderParam("Authorization") String authorization,
                                  PaginationRequest pagiRequest) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserId(user.getId());
    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.or();
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getAssignee())) {
      taskQuery = taskQuery.taskAssigneeLike("%"+pagiRequest.getFilter().getAssignee()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingTypeName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_type", pagiRequest.getFilter().getBuildingTypeName());
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_name", "%"+pagiRequest.getFilter().getBuildingName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getName())) {
      taskQuery = taskQuery.taskNameLike("%"+pagiRequest.getFilter().getName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getCertificationType())) {
      taskQuery = taskQuery.processVariableValueLike("certification_type", "%"+pagiRequest.getFilter().getCertificationType()+"%");
    }
    taskQuery = taskQuery.endOr();
    String[] processInstanceIds = projectVerificators.stream().map(pv -> pv.getProcessInstanceID()).toArray(String[]::new);
    taskQuery = taskQuery.processInstanceIdIn(processInstanceIds);
    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();

    List<Task> tasks = taskQuery.listPage(pagiRequest.getPage(), pagiRequest.getSize());
    Long taskSize = taskQuery.count();

    List<Tenant> tenants = tenantService.findAll();
    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      String tenantId = sbhTask.getTenantId();
      Tenant selectedTenant = tenants.stream().filter(tnt -> tnt.getId().equals(tenantId)).findFirst().get();
      sbhTask.setTenantName(selectedTenant.getName());
      final SbhTask innerTask = sbhTask;
      Boolean assigned = projectVerificators.stream().anyMatch(project -> project.getProcessInstanceID().equals(innerTask.getProcessInstanceId()));
      sbhTask.setAssigned(assigned);

      sbhTasks.add(sbhTask);
    }

    PaginationResult result = new PaginationResult(sbhTasks, taskSize);
    String json = new Gson().toJson(result);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/tasks/client/pagi")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response ClientPaginationTasks(@HeaderParam("Authorization") String authorization,
                                  PaginationRequest pagiRequest) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    List<ProjectUser> projectUsers = projectUserService.findByUserId(user.getUsername());

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.or();
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getAssignee())) {
      taskQuery = taskQuery.taskAssigneeLike("%"+pagiRequest.getFilter().getAssignee()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingTypeName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_type", pagiRequest.getFilter().getBuildingTypeName());
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_name", "%"+pagiRequest.getFilter().getBuildingName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getName())) {
      taskQuery = taskQuery.taskNameLike("%"+pagiRequest.getFilter().getName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getCertificationType())) {
      taskQuery = taskQuery.processVariableValueLike("certification_type", "%"+pagiRequest.getFilter().getCertificationType()+"%");
    }
    taskQuery = taskQuery.endOr();
    taskQuery = taskQuery.processVariableValueEquals("tenant", user.getTenant().getId());
    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();

    List<Task> tasks = taskQuery.listPage(pagiRequest.getPage(), pagiRequest.getSize());
    Long taskSize = taskQuery.count();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
        final SbhTask innerTask = sbhTask;
        Boolean assigned = projectUsers.stream().anyMatch(project -> project.getProcessInstanceID().equals(innerTask.getProcessInstanceId()));
        sbhTask.setAssigned(assigned);
      }
      sbhTasks.add(sbhTask);
    }

    PaginationResult result = new PaginationResult(sbhTasks, taskSize);
    String json = new Gson().toJson(result);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/tasks/client/own_task/pagi")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response ClientPaginationOwnTasks(@HeaderParam("Authorization") String authorization,
                                  PaginationRequest pagiRequest) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<ProjectUser> projectUsers = projectUserService.findByUserId(user.getUsername());

    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.or();
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getAssignee())) {
      taskQuery = taskQuery.taskAssigneeLike("%"+pagiRequest.getFilter().getAssignee()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingTypeName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_type", pagiRequest.getFilter().getBuildingTypeName());
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_name", "%"+pagiRequest.getFilter().getBuildingName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getName())) {
      taskQuery = taskQuery.taskNameLike("%"+pagiRequest.getFilter().getName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getCertificationType())) {
      taskQuery = taskQuery.processVariableValueLike("certification_type", "%"+pagiRequest.getFilter().getCertificationType()+"%");
    }
    taskQuery = taskQuery.endOr();
    taskQuery = taskQuery.processVariableValueEquals("tenant", user.getTenant().getId());
    String[] processInstanceIds = projectUsers.stream().map(pu -> pu.getProcessInstanceID()).toArray(String[]::new);
    taskQuery = taskQuery.processInstanceIdIn(processInstanceIds);
    taskQuery = taskQuery.taskDefinitionKeyIn(clientTasks);
    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();

    List<Task> tasks = taskQuery.listPage(pagiRequest.getPage(), pagiRequest.getSize());
    Long taskSize = taskQuery.count();

    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
        final SbhTask innerTask = sbhTask;
        Boolean assigned = projectUsers.stream().anyMatch(project -> project.getProcessInstanceID().equals(innerTask.getProcessInstanceId()));
        sbhTask.setAssigned(assigned);
      }
      sbhTasks.add(sbhTask);
    }

    PaginationResult result = new PaginationResult(sbhTasks, taskSize);
    String json = new Gson().toJson(result);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/tasks/verificator/own_task/pagi")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response VerificatorPaginationOwnTasks(@HeaderParam("Authorization") String authorization,
                                  PaginationRequest pagiRequest) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserId(user.getId());

    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.or();
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getAssignee())) {
      taskQuery = taskQuery.taskAssigneeLike("%"+pagiRequest.getFilter().getAssignee()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingTypeName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_type", pagiRequest.getFilter().getBuildingTypeName());
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_name", "%"+pagiRequest.getFilter().getBuildingName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getName())) {
      taskQuery = taskQuery.taskNameLike("%"+pagiRequest.getFilter().getName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getCertificationType())) {
      taskQuery = taskQuery.processVariableValueLike("certification_type", "%"+pagiRequest.getFilter().getCertificationType()+"%");
    }
    taskQuery = taskQuery.endOr();
    String[] processInstanceIds = projectVerificators.stream().map(pv -> pv.getProcessInstanceID()).toArray(String[]::new);
    taskQuery = taskQuery.processInstanceIdIn(processInstanceIds);
    taskQuery = taskQuery.taskDefinitionKeyIn(verificatorTasks);
    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();

    List<Task> tasks = taskQuery.listPage(pagiRequest.getPage(), pagiRequest.getSize());
    Long taskSize = taskQuery.count();

    List<Tenant> tenants = tenantService.findAll();
    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      String tenantId = sbhTask.getTenantId();
      Tenant selectedTenant = tenants.stream().filter(tnt -> tnt.getId().equals(tenantId)).findFirst().get();
      sbhTask.setTenantName(selectedTenant.getName());
      final SbhTask innerTask = sbhTask;
      if (user.getGroupId().equals("verificator")) {
        Boolean assigned = projectVerificators.stream().anyMatch(project -> project.getProcessInstanceID().equals(innerTask.getProcessInstanceId()));
        sbhTask.setAssigned(assigned);
      } else {
        sbhTask.setAssigned(true);
      }

      sbhTasks.add(sbhTask);
    }

    PaginationResult result = new PaginationResult(sbhTasks, taskSize);
    String json = new Gson().toJson(result);
    return Response.ok(json).build();
  }

  @POST
  @Path(value = "/tasks/admin/own_task/pagi")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response AdminPaginationOwnTasks(@HeaderParam("Authorization") String authorization,
                                  PaginationRequest pagiRequest) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    List<SbhTask> sbhTasks =  new ArrayList<SbhTask>();
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    taskQuery = taskQuery.or();
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getAssignee())) {
      taskQuery = taskQuery.taskAssigneeLike("%"+pagiRequest.getFilter().getAssignee()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingTypeName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_type", pagiRequest.getFilter().getBuildingTypeName());
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getBuildingName())) {
      taskQuery =  taskQuery.processVariableValueLike("building_name", "%"+pagiRequest.getFilter().getBuildingName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getName())) {
      taskQuery = taskQuery.taskNameLike("%"+pagiRequest.getFilter().getName()+"%");
    }
    if (!StringUtils.isEmpty(pagiRequest.getFilter().getCertificationType())) {
      taskQuery = taskQuery.processVariableValueLike("certification_type", "%"+pagiRequest.getFilter().getCertificationType()+"%");
    }
    taskQuery = taskQuery.endOr();
    taskQuery = taskQuery.taskDefinitionKeyIn(adminTasks);
    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();

    List<Task> tasks = taskQuery.listPage(pagiRequest.getPage(), pagiRequest.getSize());
    Long taskSize = taskQuery.count();

    List<Tenant> tenants = tenantService.findAll();
    for (Task task : tasks) {
      SbhTask sbhTask = SbhTask.CreateFromTask(task);
      Map<String, Object> variableMap = taskService.getVariables(task.getId());
      sbhTask = SbhTask.AssignTaskVariables(sbhTask, variableMap);
      if (NumberUtils.isCreatable(sbhTask.getBuildingType())) {
        BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(sbhTask.getBuildingType()));
        sbhTask.setBuildingTypeName(buildingType.getNameId());
      }
      String tenantId = sbhTask.getTenantId();
      Tenant selectedTenant = tenants.stream().filter(tnt -> tnt.getId().equals(tenantId)).findFirst().get();
      sbhTask.setTenantName(selectedTenant.getName());
      sbhTask.setAssigned(true);
      sbhTasks.add(sbhTask);
    }

    PaginationResult result = new PaginationResult(sbhTasks, taskSize);
    String json = new Gson().toJson(result);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/tasks/verificator/pagi/count")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response VerificatorPaginationTaskCounts(@HeaderParam("Authorization") String authorization) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserId(user.getId());
    TaskQuery taskQuery = taskService.createTaskQuery();
    taskQuery = taskQuery.matchVariableValuesIgnoreCase();

    String[] processInstanceIds = projectVerificators.stream().map(pv -> pv.getProcessInstanceID()).toArray(String[]::new);
    taskQuery = taskQuery.processInstanceIdIn(processInstanceIds);
    taskQuery = taskQuery.active().orderByTaskCreateTime().desc();
    Long taskSize = taskQuery.count();

    String json = new Gson().toJson(taskSize);
    return Response.ok(json).build();
  }

  @GET
  @Path(value = "/variables/client/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ClientVariable(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
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
    Map<String, Object> variableMap = new HashMap<String, Object>();
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      variableMap = taskService.getVariables(taskId);
    } catch (Exception e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    List<ProjectUser> projectUsers = projectUserService.findByUserIdAndProcessInstanceID(user.getUsername(), task.getProcessInstanceId());
    if (projectUsers.size() <= 0) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "User not assigned or owned these project");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    if (!variableMap.get("tenant").equals(user.getTenant().getId())) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not eligible to access for current user");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    variableMap.put("task_id", task.getId());
    variableMap.put("created_at", task.getCreateTime());
    variableMap.put("due_date", task.getDueDate());
    variableMap.put("definition_key", task.getTaskDefinitionKey());
    if (task.getTaskDefinitionKey().equals("final-assessment-letter")) {
      variableMap.put("task_name", "Final Assessment Certificate");
    } else {
      variableMap.put("task_name", task.getName());
    }

    if (variableMap.get("province") != null) {
      String provinceId = String.valueOf(variableMap.get("province"));
      Province province = provinceService.findById(Integer.parseInt(provinceId));
      variableMap.put("province_name", province.getName());
    }

    if (variableMap.get("city") != null) {
      String cityId = String.valueOf(variableMap.get("city"));
      City city = cityService.findById(Integer.parseInt(cityId));
      variableMap.put("city_name", city.getName());
    }

    try {
      BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(String.valueOf(variableMap.get("building_type"))));
      variableMap.put("building_type_name", buildingType.getNameId());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }

    return Response.ok(variableMap).build();
  }

  @GET
  @Path(value = "/variables/admin/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response AdminVariable(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {
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
    Map<String, Object> variableMap = new HashMap<String, Object>();
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      variableMap = taskService.getVariables(taskId);
    } catch (Exception e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("admin", "camunda-admin", "verificator"));
    if (!adminRoles.stream().anyMatch(role -> role.equals(user.getGroup().getId()))) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Only administrator permitted");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    if (user.getGroupId().equals("verificator")) {
      List<ProjectVerificator> projectVerificators = projectVerificatorService.findByUserIdAndProcessInstanceID(user.getId(), task.getProcessInstanceId());
      if (projectVerificators.size() <= 0) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Verificator not assigned or owned these project");
        String json = new Gson().toJson(map);
  
        return Response.status(400).entity(json).build();
      }
    }

    variableMap.put("task_id", task.getId());
    variableMap.put("created_at", task.getCreateTime());
    variableMap.put("due_date", task.getDueDate());
    variableMap.put("definition_key", task.getTaskDefinitionKey());
    if (task.getTaskDefinitionKey().equals("final-assessment-letter")) {
      variableMap.put("task_name", "Final Assessment Certificate");
    } else {
      variableMap.put("task_name", task.getName());
    }

    if (variableMap.get("province") != null) {
      String provinceId = String.valueOf(variableMap.get("province"));
      Province province = provinceService.findById(Integer.parseInt(provinceId));
      variableMap.put("province_name", province.getName());
    }

    if (variableMap.get("city") != null) {
      String cityId = String.valueOf(variableMap.get("city"));
      City city = cityService.findById(Integer.parseInt(cityId));
      variableMap.put("city_name", city.getName());
    }

    try {
      BuildingType buildingType = buildingTypeService.findById(Integer.parseInt(String.valueOf(variableMap.get("building_type"))));
      variableMap.put("building_type_name", buildingType.getNameId());
    } catch (Exception e) {
      logger.error(e.getMessage());
    }

    return Response.ok(variableMap).build();
  }

  @POST
  @Path(value = "/read-task")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response ReadTask(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    taskService.setVariable(taskId, "read", true);
    return Response.ok().build();
  }

  @POST
  @Path(value = "/accept-task")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response AcceptTask(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String taskDefinitionKey = task.getTaskDefinitionKey();
    switch(taskDefinitionKey) {
      case "check-first-payment":
        taskService.setVariable(taskId, "first_payment_paid", true);
        break;
      case "check-second-payment":
        taskService.setVariable(taskId, "second_payment_paid", true);
        boolean designRecognition = (Boolean) taskService.getVariable(taskId, "design_recognition");
        if (designRecognition) {
          String processInstanceId = task.getProcessInstanceId();
          transactionCreationService.createDRTransactionForProcessInstance(processInstanceId);
        } else {
          String processInstanceId = task.getProcessInstanceId();
          transactionCreationService.createFATransactionForProcessInstance(processInstanceId); 
        }
        break;
      case "check-third-payment":
        taskService.setVariable(taskId, "third_payment_paid", true);
        break;
      case "check-third-payment-fa":
        taskService.setVariable(taskId, "third_payment_paid", true);
        break;
      case "design-recognition-revision-review":
        taskService.setVariable(taskId, "approved_dr_review", true);
        break;
      case "design-recognition-letter":
        String processInstanceId = task.getProcessInstanceId();
        transactionCreationService.createFATransactionForProcessInstance(processInstanceId);
        break;
      case "final-assessment-revision-review":
        taskService.setVariable(taskId, "approved_fa_review", true);
        break;
      case "on-site-verification":
        taskService.setVariable(taskId, "on_site_approved", true);
        break;
    }

    taskService.setVariable(taskId, "approved", true);
    taskService.setVariable(taskId, "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(taskId, "admin");
    }
    taskService.complete(taskId);

    return Response.ok().build();
  }

  @POST
  @Path(value = "/reject-task")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response RejectTask(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("rejected_reason") String rejectedReason
  ) {
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
    String processInstanceId = task.getProcessInstanceId();
    String taskDefinitionKey = task.getTaskDefinitionKey();
    switch(taskDefinitionKey) {
      case "check-first-payment":
        taskService.setVariable(taskId, "first_payment_paid", false);
        break;
      case "check-second-payment":
        taskService.setVariable(taskId, "second_payment_paid", false);
        break;
      case "check-third-payment":
        taskService.setVariable(taskId, "third_payment_paid", false);
        break;
      case "check-third-payment-fa":
        taskService.setVariable(taskId, "third_payment_paid", false);
        break;
      case "design-recognition-revision-review":
        taskService.setVariable(taskId, "approved_dr_review", false);
        break;
      case "final-assessment-revision-review":
        taskService.setVariable(taskId, "approved_fa_review", false);
        break;
      case "on-site-verification":
        taskService.setVariable(taskId, "on_site_approved", false);
        break;
    }

    taskService.setVariable(taskId, "approved", false);
    taskService.setVariable(taskId, "rejected_reason", rejectedReason);
    taskService.setVariable(taskId, "read", false);
    if (!Objects.nonNull(task.getAssignee())) {
      taskService.claim(taskId, "admin");
    }
    taskService.complete(taskId);

    task = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().singleResult();
    String assignee = taskService.getVariable(task.getId(), "assignee").toString();
    task.setAssignee(assignee);
    taskService.claim(task.getId(), assignee);

    mailerService.SendRejectionEmail(rejectedReason, task);

    return Response.ok().build();
  }

  @DELETE
  @Path(value = "/tasks/{taskId}/reason/{reason}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DeleteTask(
    @PathParam("taskId") String taskId, 
    @PathParam("reason") String reason,
    @HeaderParam("Authorization") String authorization) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ArrayList<String> adminRoles = new ArrayList<String>(Arrays.asList("camunda-admin"));
    if (!adminRoles.stream().anyMatch(role -> role.equals(user.getGroup().getId()))) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Only super administrator permitted");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
 
    Task task;
    Map<String, Object> variableMap = new HashMap<String, Object>();
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      variableMap = taskService.getVariables(taskId);
    } catch (Exception e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Project not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    String processInstanceId = task.getProcessInstanceId();

    try {
      runtimeService.deleteProcessInstance(processInstanceId, reason);
    } catch (AuthorizationException e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "User not authorized to delete this project");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    } catch (ProcessEngineException e) {
      logger.error(e.getMessage());
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "Selected project not found to delete");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    return Response.ok(variableMap).build();
  }

  @GET
  @Path(value = "/tasks/history/{task_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response PreviousToCurrentTask(
    @PathParam("task_id") String taskId,
    @HeaderParam("Authorization") String authorization
  ) {
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    HistoryService historyService = processEngine.getHistoryService();

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

    List<HistoricActivityInstance> histories = historyService.createHistoricActivityInstanceQuery().
                                                              activityType("userTask").
                                                              processInstanceId(task.getProcessInstanceId()).
                                                              orderByHistoricActivityInstanceStartTime().desc().
                                                              list();

    histories = histories.stream().
    filter(distinctByKey(s -> Arrays.asList(s.getActivityId()))).
    collect(Collectors.toList());
    return Response.ok(histories).build();
  }

  @POST
  @Path(value = "/tasks/rollback")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response RollbackTask(
    @FormDataParam("task_id") String taskId, 
    @FormDataParam("started_activity_id") String startedActivityId,
    @FormDataParam("ancestor_activity_instance_id") String ancestorActivityInstanceId,
    @HeaderParam("Authorization") String authorization
  ) {
    User user = userService.GetUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();

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

    // runtimeService.createProcessInstanceModification(task.getProcessInstanceId()).cancelAllForActivity(startedActivityId);

    // List<HistoricActivityInstance> histories = historyService.createHistoricActivityInstanceQuery().
    //           processInstanceId(task.getProcessInstanceId()).
    //           orderByHistoricActivityInstanceStartTime().desc().
    //           list();
    // for(HistoricActivityInstance activity : histories) {
    //   activity.getActivityId()
    // }

    // runtimeService.suspendProcessInstanceById(task.getProcessInstanceId());
    // taskService.deleteTask(task.getId());
    // runtimeService.activateProcessInstanceById(task.getProcessInstanceId());

    // taskService.deleteTask(task.getId());
    // ProcessInstanceModificationInstantiationBuilder process = runtimeService.createProcessInstanceModification(task.getProcessInstanceId())
    //               .startBeforeActivity(startedActivityId);

    // List<HistoricActivityInstance> histories = historyService.createHistoricActivityInstanceQuery().
    //               processInstanceId(task.getProcessInstanceId()).
    //               activityType("userTask").
    //               orderByHistoricActivityInstanceStartTime().desc().
    //               list();
    // HistoricActivityInstance selectedInstance = histories.stream().filter(history -> history.getActivityId().equals(startedActivityId)).findFirst().get();
    
    ActivityInstance activityInstance = runtimeService.getActivityInstance(task.getProcessInstanceId());
    ActivityInstance[] activityInstances = activityInstance.getChildActivityInstances();

    runtimeService.createProcessInstanceModification(task.getProcessInstanceId()).
    cancelActivityInstance(activityInstances[0].getId()).
    startBeforeActivity(startedActivityId).
    execute();

    // ProcessInstanceModificationBuilder process = runtimeService.createProcessInstanceModification(task.getProcessInstanceId());
              
    // for(HistoricActivityInstance activity : histories) {
    //   if (activity.getActivityId().equals(startedActivityId)) {
    //     process.cancelActivityInstance(activityId)
    //     break;
    //   } else {
    //     process.cancelAllForActivity(activity.getActivityId());
    //   }
    // }

  
    return Response.ok().build();
  }

  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }
}
