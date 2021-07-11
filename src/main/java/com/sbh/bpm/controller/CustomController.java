package com.sbh.bpm.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;

import camundajar.impl.com.google.gson.Gson;


@Path(value = "/custom")
public class CustomController {
  @GET
  @Path(value = "/message")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getProcessEngines(@HeaderParam("Authorization") String authorization) {        
    return Response.ok("welcome").build();
  }

  @GET
  @Path(value = "/activetask")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getOther(@HeaderParam("Authorization") String authorization) {      
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    // RepositoryService repositoryService = processEngine.getRepositoryService();

    List<Task> tasks = processEngine.getTaskService().createTaskQuery().
    taskDefinitionKey("check-builiding-information").
    active().list();
    Collection<String> taskIdList = new LinkedList<String>();
    tasks.forEach((e) -> taskIdList.add(e.getId()));
    List<VariableInstance> varList = runtimeService.createVariableInstanceQuery().taskIdIn(taskIdList.toArray(new String[taskIdList.size()])).list();

    String json = new Gson().toJson(tasks);
    return Response.ok(json).build();
  }
}
