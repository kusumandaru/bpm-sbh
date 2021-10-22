package com.sbh.bpm.controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.gson.Gson;
import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.service.GoogleCloudStorage;
import com.sbh.bpm.service.IBuildingTypeService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IMasterAdminService;
import com.sbh.bpm.service.IPdfGeneratorUtil;
import com.sbh.bpm.service.IProvinceService;
import com.sbh.bpm.service.ISequenceNumberService;
import com.sbh.bpm.service.SequenceNumberService.NUMBER_FORMAT;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NullValueException;
import org.camunda.bpm.engine.task.Task;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.gson.Gson;
import com.sbh.bpm.model.BuildingType;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.service.GoogleCloudStorage;
import com.sbh.bpm.service.IBuildingTypeService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IMasterAdminService;
import com.sbh.bpm.service.IPdfGeneratorUtil;
import com.sbh.bpm.service.IProvinceService;
import com.sbh.bpm.service.ISequenceNumberService;
import com.sbh.bpm.service.SequenceNumberService;
import com.sbh.bpm.service.SequenceNumberService.NUMBER_FORMAT;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.repository.ProcessDefinition;

@Path(value = "/new-building")
public class FileController extends GcsUtil{
  private static final Logger logger = LogManager.getLogger(NewBuildingController.class);
  
  @Autowired
  private IProvinceService provinceService;

  @Autowired
  private ICityService cityService;

  @Autowired
  private IMailerService mailerService;

  @Autowired
  private IPdfGeneratorUtil pdfGeneratorUtil;

  @Autowired
  private ISequenceNumberService sequenceNumberService;

  @Autowired
  private IMasterAdminService masterAdminService;
 
  
  @GET
  @Path(value = "/archived_files/{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ArchivedFile(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    Map<String, Object> variableMap;
    Task task;
    String processInstanceId;
    try {
      variableMap = taskService.getVariables(taskId);
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (NullValueException e) {
      return Response.status(400, "task id not found").build();
    }

    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Blob>> listOfCallable = Arrays.asList(
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "proof_of_payment"),
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "building_plan"),
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "rt_rw"),
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "upl_ukl"),
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "earthquake_resistance"),
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "disability_friendly"),
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "safety_and_fire_requirement"),
                () -> GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, "study_case_readiness")
                );

    FileOutputStream fos;
    ZipOutputStream zipOut;
    try {
      fos = new FileOutputStream(taskId + ".zip");
      zipOut = new ZipOutputStream(fos);
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
    try {
      List<Future<Blob>> futures = executor.invokeAll(listOfCallable);

      futures.stream().forEach(f -> {
          try {
            Blob blob = f.get();
            if (blob != null) {
              ZipEntry zipEntry = new ZipEntry(blob.getName());
              zipOut.putNextEntry(zipEntry);
              byte[] byteArray = blob.getContent();
              zipOut.write(byteArray);
            }
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
      });

    } catch (InterruptedException e) {// thread was interrupted
        logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();

    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    try {
      zipOut.close();
      fos.close();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
  

    File zipFile = new File(taskId + ".zip");
    StreamingOutput stream = new StreamingOutput() {
        @Override
        public void write(OutputStream output) throws IOException {
            try {
                output.write(IOUtils.toByteArray(new FileInputStream(zipFile)));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    };

    return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "inline; filename=\"" + zipFile.getName() + "\"") 
        .build();
  }

  @GET
  @Path(value = "/diagram/{processDefinitionId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetDiagram(@PathParam("processDefinitionId") String processDefinitionId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    
    ProcessDefinition definition = processEngine.getRepositoryService().getProcessDefinition(processDefinitionId);
    String fileName = definition.getDiagramResourceName();

    return Response.ok(fileName).build();
  }

  @GET
  @Path(value = "/url_file/{task_id}/{file_name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetUrlFile(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId,
    @PathParam("file_name") String fileName
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    
    Task task;
    String processInstanceId;
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (NullValueException e) {
      return Response.status(400, "task id not found").build();
    }

    Pair<String, String> result;
    try {
      result = GetUrlGcs(variableMap, processInstanceId, fileName);
    } catch (IOException e) {
      result = null;
      return Response.status(404).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result.getValue());
    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/eligibility_statement/{taskId}")
  @Produces({"application/pdf"})
  public javax.ws.rs.core.Response getEligibilityStatement(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    
    Task task;
    String processInstanceId;
    String activityInstanceId;
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
      activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    String fileName = "eligibility_statement";
    Pair<String, String> result;
    byte[] bytes;

    if (variableMap.get(fileName) != null) {
      try {
        Blob blob = GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, fileName);
        bytes = googleCloudStorage.getContent(blob.getBlobId());
      } catch (Exception e) {
        result = null;
        return Response.status(404, e.getMessage()).build();
      }
    } else {
      int style = DateFormat.LONG;
      //Also try with style = DateFormat.FULL and DateFormat.SHORT and DateFormat.MEDIUM
      Date date = new Date();
      DateFormat df;
      Locale localeIndonesia = new Locale("id", "ID");
      df = DateFormat.getDateInstance(style, localeIndonesia);
      variableMap.put("printAt",df.format(date));
  
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
  
      variableMap.put("eli_letter_number", sequenceNumberService.getNextNumber("ELI", SequenceNumberService.NUMBER_FORMAT.GENERAL));
      taskService.setVariable(task.getId(), "eli_letter_number", variableMap.get("eli_letter_number"));

      variableMap.put("manager_name", masterAdminService.findLast().getManagerName());
      try {
        Map<String, Object> maps = masterAdminService.getVariableMap();
        result = GetUrlGcs(maps, "admin", "manager_signature");
        variableMap.put("manager_signature", result.getValue());
        logger.info(result.getValue());
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
  
      bytes = pdfGeneratorUtil.CreatePdf("eligibility-statement", variableMap);

      try {
        UploadToGcs(runtimeService, processInstanceId, activityInstanceId, bytes, fileName, "pdf");
      } catch (IOException e) {
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();
      }
    }

    /* Send the response as downloadable PDF */
    javax.ws.rs.core.Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok(bytes);
    responseBuilder.type("application/pdf");
    responseBuilder.header("Content-Disposition", "attachment; filename=eligible-statement.pdf");
    return responseBuilder.build();
  }

  @GET
  @Path(value = "/registered_project/{taskId}")
  @Produces({"application/pdf"})
  public javax.ws.rs.core.Response getRegisteredProject(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) {   
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    
    Task task;
    String processInstanceId;
    String activityInstanceId;
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
      activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    String fileName = "registered_project";
    Pair<String, String> result;
    byte[] bytes;
    if (variableMap.get(fileName) != null) {
      try {
        Blob blob = GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, fileName);
        bytes = googleCloudStorage.getContent(blob.getBlobId());
      } catch (Exception e) {
        result = null;
        return Response.status(404, e.getMessage()).build();
      }
    } else {
      int style = DateFormat.LONG;
      //Also try with style = DateFormat.FULL and DateFormat.SHORT and DateFormat.MEDIUM
      Date date = new Date();
      DateFormat df;
      Locale localeIndonesia = new Locale("id", "ID");
      df = DateFormat.getDateInstance(style, localeIndonesia);
      variableMap.put("printAt",df.format(date));
  
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
  
      variableMap.put("rpd_letter_number", sequenceNumberService.getNextNumber("RPD",  NUMBER_FORMAT.GENERAL));
      variableMap.put("registered_project_number", sequenceNumberService.getNextNumber("RP/NB", NUMBER_FORMAT.REGISTERED));
      variableMap.put("agreement_number", "002/Greenship.NB/SBH-02/IX/2021");

      taskService.setVariable(task.getId(), "rpd_letter_number", variableMap.get("rpd_letter_number"));
      taskService.setVariable(task.getId(), "registered_project_number", variableMap.get("registered_project_number"));

      variableMap.put("manager_name", masterAdminService.findLast().getManagerName());
      try {
        Map<String, Object> maps = masterAdminService.getVariableMap();
        result = GetUrlGcs(maps, "admin", "manager_signature");
        variableMap.put("manager_signature", result.getValue());
        logger.info(result.getValue());
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
  
      bytes = pdfGeneratorUtil.CreatePdf("registered-project", variableMap);

      try {
        UploadToGcs(runtimeService, processInstanceId, activityInstanceId, bytes, fileName, "pdf");
      } catch (IOException e) {
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();
      }
    }

    /* Send the response as downloadable PDF */
    javax.ws.rs.core.Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok(bytes);
    responseBuilder.type("application/pdf");
    responseBuilder.header("Content-Disposition", "attachment; filename=registered-project.pdf");
    return responseBuilder.build();
  }

  @GET
  @Path(value = "/design_recognition_statement/{taskId}")
  @Produces({"application/pdf"})
  public javax.ws.rs.core.Response geDesignRecognitionStatement(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    
    Task task;
    String processInstanceId;
    String activityInstanceId;
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
      activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    String fileName = "design_recognition_statement";
    Pair<String, String> result;
    byte[] bytes;
    if (variableMap.get(fileName) != null) {
      try {
        Blob blob = GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, fileName);
        bytes = googleCloudStorage.getContent(blob.getBlobId());
      } catch (Exception e) {
        result = null;
        return Response.status(404, e.getMessage()).build();
      }
    } else {
      int style = DateFormat.LONG;
      //Also try with style = DateFormat.FULL and DateFormat.SHORT and DateFormat.MEDIUM
      Date date = new Date();
      DateFormat df;
      Locale localeIndonesia = new Locale("id", "ID");
      df = DateFormat.getDateInstance(style, localeIndonesia);
      variableMap.put("printAt",df.format(date));
  
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
  
      variableMap.put("dr_statement_letter_number", sequenceNumberService.getNextNumber("DR",  NUMBER_FORMAT.GENERAL));
      taskService.setVariable(task.getId(), "dr_statement_letter_number", variableMap.get("dr_statement_letter_number"));

      // TODO change to variable
      variableMap.put("evaluation_assesment_date",df.format(date));
      variableMap.put("company_name", "PT Sejahtera Sentosa");
      variableMap.put("building_rank", "GOLD");
      variableMap.put("reviewer_name", "Anggia Murni");
      variableMap.put("director_name", "Lucia Karina");

      variableMap.put("manager_name", masterAdminService.findLast().getManagerName());
      try {
        Map<String, Object> maps = masterAdminService.getVariableMap();
        result = GetUrlGcs(maps, "admin", "manager_signature");

        //TODO change to proper signature
        variableMap.put("director_signature", result.getValue());
        variableMap.put("reviewer_signature", result.getValue());
        logger.info(result.getValue());
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
  
      bytes = pdfGeneratorUtil.CreatePdf("design-recognition-statement", variableMap);

      try {
        UploadToGcs(runtimeService, processInstanceId, activityInstanceId, bytes, fileName, "pdf");
      } catch (IOException e) {
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();
      }
    }

    /* Send the response as downloadable PDF */
    javax.ws.rs.core.Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok(bytes);
    responseBuilder.type("application/pdf");
    responseBuilder.header("Content-Disposition", "attachment; filename=design-recognition-statement.pdf");
    return responseBuilder.build();
  }

  @GET
  @Path(value = "/design_recognition_result/{taskId}")
  @Produces({"application/pdf"})
  public javax.ws.rs.core.Response geDesignRecognitionResult(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    
    Task task;
    String processInstanceId;
    String activityInstanceId;
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
      activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();
    } catch (NullValueException e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    GoogleCloudStorage googleCloudStorage;
    try {
      googleCloudStorage = new GoogleCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    String fileName = "design_recognition_result";
    Pair<String, String> result;
    byte[] bytes;
    if (variableMap.get(fileName) != null) {
      try {
        Blob blob = GetBlobDirect(googleCloudStorage, variableMap, processInstanceId, fileName);
        bytes = googleCloudStorage.getContent(blob.getBlobId());
      } catch (Exception e) {
        result = null;
        return Response.status(404, e.getMessage()).build();
      }
    } else {
      int style = DateFormat.LONG;
      //Also try with style = DateFormat.FULL and DateFormat.SHORT and DateFormat.MEDIUM
      Date date = new Date();
      DateFormat df;
      Locale localeIndonesia = new Locale("id", "ID");
      df = DateFormat.getDateInstance(style, localeIndonesia);
      variableMap.put("printAt",df.format(date));
  
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
  
      variableMap.put("dr_result_letter_number", sequenceNumberService.getNextNumber("DR",  NUMBER_FORMAT.GENERAL));
      taskService.setVariable(task.getId(), "dr_result_letter_number", variableMap.get("dr_result_letter_number"));

      // TODO change to variable
      variableMap.put("evaluation_assesment_date",df.format(date));
      variableMap.put("company_name", "PT Sejahtera Sentosa");
      variableMap.put("building_rank", "GOLD");
      variableMap.put("director_name", "Lucia Karina");
      variableMap.put("dr_certification_number", "RP/NB/Cer/086/III/2020");
      variableMap.put("project_type", "Greenship NB 1.2");
      variableMap.put("dr_total_score", 49);
      Map<String, Integer> drResults = new HashMap<String, Integer>();
      drResults.put("ASD (Appropiate Site Development)", 11);
      drResults.put("EEC (Energy Efficiency and Conservation)", 10);
      drResults.put("WAC (Water Conservation)", 19);
      drResults.put("MRC (Material Recource and Cycle)", 2);
      drResults.put("IHC (Indoor Healt and Comfort)", 4);
      drResults.put("BEM (Building Environment Management)", 3);
      variableMap.put("dr_results", drResults);

      variableMap.put("manager_name", masterAdminService.findLast().getManagerName());
      try {
        Map<String, Object> maps = masterAdminService.getVariableMap();
        result = GetUrlGcs(maps, "admin", "manager_signature");

        //TODO change to proper signature
        variableMap.put("director_signature", result.getValue());
        logger.info(result.getValue());
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
  
      bytes = pdfGeneratorUtil.CreatePdf("design-recognition-result", variableMap);

      try {
        UploadToGcs(runtimeService, processInstanceId, activityInstanceId, bytes, fileName, "pdf");
      } catch (IOException e) {
        logger.error(e.getMessage());
        return Response.status(400, e.getMessage()).build();
      }
    }

    /* Send the response as downloadable PDF */
    javax.ws.rs.core.Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok(bytes);
    responseBuilder.type("application/pdf");
    responseBuilder.header("Content-Disposition", "attachment; filename=design-recognition-result.pdf");
    return responseBuilder.build();
  }

}