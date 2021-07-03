package com.sbh.bpm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.json.JSONException;
import org.json.JSONObject;

import camundajar.impl.com.google.gson.Gson;

@Path(value = "/new-building")
public class NewBuildingController {
  @GET
  @Path(value = "/start")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getOther(@HeaderParam("Authorization") String authorization) {      
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    // // RepositoryService repositoryService = processEngine.getRepositoryService();

    // List<Task> tasks = processEngine.getTaskService().createTaskQuery().active().list();
    // String json = new Gson().toJson(tasks);
    // return Response.ok(json).build();

    Map<String, Object> variables = new HashMap<String,Object>();
    variables.put("certification_type", "new_building");
    variables.put("building_type", "office");
    variables.put("building_name", "Tanah Kusir Tower");
    variables.put("pic", "Doddy Alfansyah");
    variables.put("owner", "PT ABC");
    variables.put("building_address", "Jalan Sudirman 12");
    variables.put("province", "DKI Jakarta");
    variables.put("city", "Jakarta Selatan");
    variables.put("postal_code", "12345");
    variables.put("phone_number", "081111111");
    variables.put("fax_number", "081111111");
    ProcessInstance instance = runtimeService.startProcessInstanceByKey("new-building-process", variables);

    JSONObject obj = new JSONObject();
    try {
      obj.put("process_definition_id", instance.getProcessDefinitionId());
      obj.put("case_instance_id", instance.getCaseInstanceId());
      obj.put("business_key", instance.getBusinessKey());
      obj.put("suspended", instance.isSuspended());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    String json = new Gson().toJson(obj);
    return Response.ok(json).build();
  }


  @GET
  @Path(value = "/diagram/{processDefinitionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDiagram(@PathParam("processDefinitionId") String processDefinitionId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();

    ProcessDefinition definition = processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);
    InputStream processDiagram = processEngine.getRepositoryService().getProcessDiagram(processDefinitionId); 
    String fileName = definition.getDiagramResourceName();

    return Response.ok(fileName).build();
  }
}