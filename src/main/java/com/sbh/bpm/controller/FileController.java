package com.sbh.bpm.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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
import com.google.gson.Gson;
import com.sbh.bpm.heap.MemoryStats;
import com.sbh.bpm.model.Attachment;
import com.sbh.bpm.model.City;
import com.sbh.bpm.model.MasterTemplate;
import com.sbh.bpm.model.ProjectAttachment;
import com.sbh.bpm.model.Province;
import com.sbh.bpm.model.UserDetail;
import com.sbh.bpm.service.IAttachmentService;
import com.sbh.bpm.service.ICityService;
import com.sbh.bpm.service.IGoogleCloudStorage;
import com.sbh.bpm.service.IMailerService;
import com.sbh.bpm.service.IMasterAdminService;
import com.sbh.bpm.service.IMasterTemplateService;
import com.sbh.bpm.service.IPdfGeneratorUtil;
import com.sbh.bpm.service.IProjectAttachmentService;
import com.sbh.bpm.service.IProvinceService;
import com.sbh.bpm.service.ISequenceNumberService;
import com.sbh.bpm.service.IUserService;
import com.sbh.bpm.service.IZipService;
import com.sbh.bpm.service.SequenceNumberService;
import com.sbh.bpm.service.SequenceNumberService.NUMBER_FORMAT;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Task;
import org.checkerframework.checker.units.qual.A;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Path(value = "/new-building")
public class FileController extends GcsUtil{
  private static final Logger logger = LoggerFactory.getLogger(NewBuildingController.class);
  
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
 
  @Autowired
  private IProjectAttachmentService projectAttachmentService;

  @Autowired
  private IAttachmentService attachmentService;

  @Autowired
  private IUserService userService;

  @Autowired
  private IMasterTemplateService masterTemplateService;

  @Autowired
  private IZipService zipService;

  @Autowired
  private IGoogleCloudStorage cloudStorage;

  @Deprecated
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
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }


    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Blob>> listOfCallable = Arrays.asList(
                () -> GetBlobDirect(variableMap, processInstanceId, "proof_of_payment"),
                () -> GetBlobDirect(variableMap, processInstanceId, "building_plan"),
                () -> GetBlobDirect(variableMap, processInstanceId, "rt_rw"),
                () -> GetBlobDirect(variableMap, processInstanceId, "upl_ukl"),
                () -> GetBlobDirect(variableMap, processInstanceId, "earthquake_resistance"),
                () -> GetBlobDirect(variableMap, processInstanceId, "disability_friendly"),
                () -> GetBlobDirect(variableMap, processInstanceId, "safety_and_fire_requirement"),
                () -> GetBlobDirect(variableMap, processInstanceId, "study_case_readiness")
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
  @Path(value = "/memory-status")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetMemoryStatus(@HeaderParam("Authorization") String authorization) { 
    MemoryStats stats = new MemoryStats();
    stats.setHeapSize(Runtime.getRuntime().totalMemory());
    stats.setHeapMaxSize(Runtime.getRuntime().maxMemory());
    stats.setHeapFreeSize(Runtime.getRuntime().freeMemory());

    String json = new Gson().toJson(stats);
    return Response.ok().entity(json).build();
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
    
    Task task;
    String processInstanceId;
    Map<String, Object> variableMap;
    try {
      variableMap = taskService.getVariables(taskId);
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    Pair<String, String> result;
    try {
      result = GetUrlGcs(variableMap, processInstanceId, fileName);
    } catch (IOException e) {
      result = null;
      return Response.status(400).build();
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
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    String username = user.getUsername();
    String role = user.getGroup().getId();

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
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    String fileType = "eligibility_statement";
    String fileName = "eligibility_statement.pdf";

    ProjectAttachment attachment = projectAttachmentService.findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(processInstanceId, fileType);
    Pair<String, String> result;
    byte[] bytes;

    if (attachment != null) {
      try {
        Blob blob = GetBlob(attachment.getLink());
        bytes = cloudStorage.GetContent(blob.getBlobId());
      } catch (Exception e) {
        result = null;
        return Response.status(400, e.getMessage()).build();
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
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
  
      bytes = pdfGeneratorUtil.CreatePdf("eligibility-statement", variableMap);

      try {
        ContentDisposition meta = FormDataContentDisposition.name(fileName).fileName(fileName).build();
        InputStream targetStream = new ByteArrayInputStream(bytes);
        attachment = SaveWithVersion(processInstanceId, activityInstanceId, targetStream, meta, fileType, username, role);
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

  @Deprecated
  @GET
  @Path(value = "/design_recognition_statement/{taskId}")
  @Produces({"application/pdf"})
  public javax.ws.rs.core.Response geDesignRecognitionStatement(@PathParam("taskId") String taskId, @HeaderParam("Authorization") String authorization) { 
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    String username = user.getUsername();
    String role = user.getGroup().getId();
    
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
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    String fileType = "design_recognition_statement";
    String fileName = "design_recognition_statement.pdf";

    ProjectAttachment attachment = projectAttachmentService.findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(processInstanceId, fileType);
    Pair<String, String> result;
    byte[] bytes;
    if (attachment != null) {
      try {
        Blob blob = GetBlob(attachment.getLink());
        bytes = cloudStorage.GetContent(blob.getBlobId());
      } catch (Exception e) {
        result = null;
        return Response.status(400, e.getMessage()).build();
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
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
  
      bytes = pdfGeneratorUtil.CreatePdf("design-recognition-statement", variableMap);

      try {
        ContentDisposition meta = FormDataContentDisposition.name(fileName).fileName(fileName).build();
        InputStream targetStream = new ByteArrayInputStream(bytes);
        attachment = SaveWithVersion(processInstanceId, activityInstanceId, targetStream, meta, fileType, username, role);
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
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    String username = user.getUsername();
    String role = user.getGroup().getId();

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
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    String fileType = "design_recognition_result";
    String fileName = "design_recognition_result.pdf";

    ProjectAttachment attachment = projectAttachmentService.findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(processInstanceId, fileType);
    Pair<String, String> result;
    byte[] bytes;
    if (attachment != null) {
      try {
        Blob blob = GetBlob(attachment.getLink());
        bytes = cloudStorage.GetContent(blob.getBlobId());
      } catch (Exception e) {
        result = null;
        return Response.status(400, e.getMessage()).build();
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
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
  
      bytes = pdfGeneratorUtil.CreatePdf("design-recognition-result", variableMap);

      try {
        ContentDisposition meta = FormDataContentDisposition.name(fileName).fileName(fileName).build();
        InputStream targetStream = new ByteArrayInputStream(bytes);
        attachment = SaveWithVersion(processInstanceId, activityInstanceId, targetStream, meta, fileType, username, role);
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

  @POST
  @Path(value = "/project/attachments")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response ProjectAttachmentCreation(
    @HeaderParam("Authorization") String authorization,
    @FormDataParam("files") FormDataBodyPart files,
    @FormDataParam("task_id") String taskId,
    @FormDataParam("file_type") String fileType
  ) {
    UserDetail user = userService.GetCompleteUserFromAuthorization(authorization);
    if (user == null) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "login expired, please logout and relogin");
      String json = new Gson().toJson(map);
      return Response.status(400).entity(json).build();
    }
    String username = user.getUsername();
    String role = user.getGroup().getId();

    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    RuntimeService runtimeService = processEngine.getRuntimeService();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    String activityInstanceId = runtimeService.getActivityInstance(processInstanceId).getId();
    ProjectAttachment attachment = new ProjectAttachment();

    try{
      for(BodyPart part : files.getParent().getBodyParts()){
        InputStream is = part.getEntityAs(InputStream.class);
        ContentDisposition meta = part.getContentDisposition();

        if (meta.getFileName() == null){
          continue;
        }

        attachment = SaveWithVersion(processInstanceId, activityInstanceId, is, meta, fileType, username, role);
        break;
      }
    } catch (Exception e) {
      return Response.status(400, e.getMessage()).build();
    }

    String json = new Gson().toJson(attachment);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/attachments/{task_id}/{attachment_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectAttachmentUrl(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId,
    @PathParam("attachment_id") Integer attachmentId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    String processInstanceId;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    ProjectAttachment attachment = projectAttachmentService.findByProcessInstanceIDAndId(processInstanceId, attachmentId);

    String result;
    try {
      result = GetUrlGcs(attachment.getLink());
    } catch (IOException e) {
      return Response.status(400).build();
    }

    Map<String, String> map = new HashMap<String, String>();
    map.put("url", result);
    map.put("filename", attachment.getFilename());
    map.put("file_type", attachment.getFileType());
    map.put("version", attachment.getVersion().toString());

    String json = new Gson().toJson(map);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/attachments/{task_id}/{file_type}/files")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectAttachmentListByFileType(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId,
    @PathParam("file_type") String fileType
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    String processInstanceId;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    List<ProjectAttachment> attachments = projectAttachmentService.findByProcessInstanceIDAndFileType(processInstanceId, fileType);

    String json = new Gson().toJson(attachments);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/attachments/{task_id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetProjectAttachmentListByTaskId(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();

    Task task;
    String processInstanceId;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    List<ProjectAttachment> attachments = projectAttachmentService.findByProcessInstanceID(processInstanceId);

    String json = new Gson().toJson(attachments);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/attachments/{task_id}/{file_type}/latest")
  @Produces(MediaType.APPLICATION_JSON)
  public Response GetLatestProjectAttachmentListByFileType(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId,
    @PathParam("file_type") String fileType
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
 
    Task task;
    String processInstanceId;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    ProjectAttachment attachment = projectAttachmentService.findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(processInstanceId, fileType);

    String json = new Gson().toJson(attachment);
    return Response.status(200).entity(json).build();
  }

  @GET
  @Path(value = "/project/attachments/{task_id}/archived_files")
  @Produces(MediaType.APPLICATION_JSON)
  public Response ProjectAttachmentArchivedFile(@PathParam("task_id") String taskId, 
  @HeaderParam("Authorization") String authorization) { 
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    Task task;
    String processInstanceId;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
      processInstanceId = task.getProcessInstanceId();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }

    List<ProjectAttachment> projectAttachments = projectAttachmentService.findByProcessInstanceID(processInstanceId);
    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Pair<Blob, String>>> listOfCallable = new ArrayList<Callable<Pair<Blob, String>>>();

    for (ProjectAttachment projectAttachment : projectAttachments) {
      listOfCallable.add(() -> new ImmutablePair<>(GetBlob(projectAttachment.getLink()), projectAttachment.getFileType()));
    }

    FileOutputStream fos;
    ZipOutputStream zipOut;
    String zipfilename = taskId + ".zip";
    try {
      fos = new FileOutputStream(zipfilename);
      zipOut = new ZipOutputStream(fos);
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }
    try {
      List<Future<Pair<Blob, String>>> futures = executor.invokeAll(listOfCallable);

      futures.stream().forEach(f -> {
          try {
            Pair<Blob, String> result = f.get();
            Blob blob = result.getLeft();
            String fileType = result.getRight();

            if (blob != null) {
              String name = FilenameUtils.getName(blob.getName());

              ZipEntry zipEntry = new ZipEntry(fileType + '/' + name);
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
  

    File zipFile = new File(zipfilename);
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
  @Path(value = "project/attachments/{task_id}/archived_scoring/{certification_type_id}/{project_type}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response DesignRecognitionAttachmentArchived(@HeaderParam("Authorization") String authorization, 
    @PathParam("task_id") String taskId,
    @PathParam("certification_type_id") Integer certificationTypeId,
    @PathParam("project_type") String projectType
  ) {
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    
    Task task;
    try {
      task = taskService.createTaskQuery().taskId(taskId).singleResult();
    } catch (Exception e) {
      Map<String, String> map = new HashMap<String, String>();
      map.put("message", "task id not found");
      String json = new Gson().toJson(map);

      return Response.status(400).entity(json).build();
    }
    String processInstanceId = task.getProcessInstanceId();
    List<MasterTemplate> masterTemplates = masterTemplateService.findByMasterCertificationTypeIDAndProjectType(certificationTypeId, projectType);
    MasterTemplate masterTemplate = masterTemplates.get(masterTemplates.size() - 1);;
    List<Attachment> attachments = attachmentService.findByProcessInstanceIdAndMasterTemplateId(processInstanceId, masterTemplate.getId());

    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Pair<byte[], Attachment>>> listOfCallable = new ArrayList<Callable<Pair<byte[], Attachment>>>();

    for (Attachment attachment : attachments) {
      listOfCallable.add(() -> new ImmutablePair<>(GetBlobByte(attachment.getLink()), attachment));
    }

    // FileOutputStream fos;
    // ZipOutputStream zipOut;
    String zipfilename = taskId + ".zip";
    // try {
    //   fos = new FileOutputStream(zipfilename);
    //   zipOut = new ZipOutputStream(fos);
    // } catch (FileNotFoundException e) {
    //   logger.error(e.getMessage());
    //   return Response.status(400, e.getMessage()).build();
    // }

    // List<String> filenames = new ArrayList<String>();
    // String rootDir;
    // try {
    //   rootDir = Files.createTempDirectory(taskId).toFile().getAbsolutePath();
    // } catch (IOException e1) {
    //   throw new IllegalStateException(e1);
    // }

    // attachments.forEach(attachment -> {
    //   byte[] byteArray = GetBlobByte(attachment.getLink());
    //   String criteriaCode = attachment.getCriteriaCode();
    //   String filename = criteriaCode + '/' + attachment.getFilename();
    //   if (ArrayUtils.contains(filenames.toArray(), filename)) {
    //     return;
    //   }
    //   filenames.add(filename);

    //   try {
    //     java.nio.file.Path dirPath = Paths.get(rootDir +'/'+ criteriaCode + '/');
    //     java.nio.file.Files.createDirectories(dirPath);
    //     java.nio.file.Path path = Paths.get(rootDir +'/'+ criteriaCode + '/' + filename);
    //     java.nio.file.Files.write(path, byteArray);
    //   } catch (Exception e1) {
    //     throw new IllegalStateException(e1);
    //   }
    // });

    String rootDir;

    try {
      rootDir = Files.createTempDirectory(taskId).toFile().getAbsolutePath();

      List<Future<Pair<byte[], Attachment>>> futures = executor.invokeAll(listOfCallable);
      List<String> filenames = new ArrayList<String>();
      futures.parallelStream().forEach(f -> {
        try {
          Pair<byte[], Attachment> result = f.get();
          byte[] byteArray = result.getLeft();
          Attachment attachment = result.getRight();
          String criteriaCode = attachment.getCriteriaCode();
          String filename = criteriaCode + '/' + attachment.getFilename();
          if (attachment != null) {
            if (ArrayUtils.contains(filenames.toArray(), filename)) {
              return;
            }
            filenames.add(filename);
          }

          java.nio.file.Path dirPath = Paths.get(rootDir +'/'+ criteriaCode + '/');
          java.nio.file.Files.createDirectories(dirPath);
          java.nio.file.Path path = Paths.get(rootDir +'/'+ filename);
          java.nio.file.Files.write(path, byteArray);
        } catch (Exception e) {
          throw new IllegalStateException(e);
        }
      });
    } catch (Exception e1) {// thread was interrupted
      logger.error(e1.getMessage());
      return Response.status(400, e1.getMessage()).build();
    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    // zipFolder(String rootDir, String zipfilename);
    // try {
    //   List<Future<Pair<byte[], Attachment>>> futures = executor.invokeAll(listOfCallable);
    //   List<String> filenames = new ArrayList<String>();
    //   futures.parallelStream().forEach(f -> {
    //       try {
    //         Pair<byte[], Attachment> result = f.get();
    //         byte[] byteArray = result.getLeft();
    //         Attachment attachment = result.getRight();
    //         String criteriaCode = attachment.getCriteriaCode();

    //         if (attachment != null) {
    //           String filename = criteriaCode + '/' + attachment.getFilename();
    //           if (ArrayUtils.contains(filenames.toArray(), filename)) {
    //             return;
    //           }
    //           filenames.add(filename);
    //           ZipEntry zipEntry = new ZipEntry(filename);
    //           zipOut.putNextEntry(zipEntry);
    //           zipOut.write(byteArray);
    //           zipOut.closeEntry();
    //         }
    //       } catch (Exception e) {
    //         throw new IllegalStateException(e);
    //       }
    //   });

    // } catch (InterruptedException e) {// thread was interrupted
    //     logger.error(e.getMessage());
    //   return Response.status(400, e.getMessage()).build();

    // } finally {
    //     // shut down the executor manually
    //     executor.shutdown();
    // }

    // try {
    //   zipOut.close();
    //   fos.close();
    // } catch (IOException e) {
    //   logger.error(e.getMessage());
    //   return Response.status(400, e.getMessage()).build();
    // }

    FileOutputStream fos;
    ZipOutputStream zipOut;
    try {
      fos = new FileOutputStream(zipfilename);
      zipOut = new ZipOutputStream(fos);
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage());
      return Response.status(400, e.getMessage()).build();
    }

    File zipDir = new File(rootDir);
    try {
      zipService.zipFile(zipDir, zipDir.getName(), zipOut);
      zipOut.close();
      fos.close();
    } catch (IOException e1) {
      logger.error(e1.getMessage());
      return Response.status(400, e1.getMessage()).build();
    }

    File zipFile = new File(zipfilename);
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
}
