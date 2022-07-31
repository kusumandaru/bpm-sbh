package com.sbh.bpm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sbh.bpm.model.Attachment;
import com.sbh.bpm.model.MasterTemplate;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ZipService implements IZipService{
  private static final Logger logger = LoggerFactory.getLogger(ZipService.class);

  @Autowired
  private IAttachmentService attachmentService;

  @Autowired
  private IMasterTemplateService masterTemplateService;

  @Autowired
  private IGoogleCloudStorage cloudStorage;

  public void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
    if (fileToZip.isHidden()) {
        return;
    }
    if (fileToZip.isDirectory()) {
        if (fileName.endsWith("/")) {
            zipOut.putNextEntry(new ZipEntry(fileName));
            zipOut.closeEntry();
        } else {
            zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            zipOut.closeEntry();
        }
        File[] children = fileToZip.listFiles();
        for (File childFile : children) {
            zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
        }
        return;
    }
    FileInputStream fis = new FileInputStream(fileToZip);
    ZipEntry zipEntry = new ZipEntry(fileName);
    zipOut.putNextEntry(zipEntry);
    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
        zipOut.write(bytes, 0, length);
    }
    fis.close();
  }

  @Async("threadPoolTaskExecutor")
  public CompletableFuture<Boolean> CreateProjectAttachmentArchived(Task task, Integer certificationTypeId, String projectType) throws Exception {
    String taskId = task.getId();
    String processInstanceId = task.getProcessInstanceId();
    List<MasterTemplate> masterTemplates = masterTemplateService.findByMasterCertificationTypeIDAndProjectType(certificationTypeId, projectType);
    MasterTemplate masterTemplate = masterTemplates.get(masterTemplates.size() - 1);;
    List<Attachment> attachments = attachmentService.findByProcessInstanceIdAndMasterTemplateId(processInstanceId, masterTemplate.getId());

    ExecutorService executor = Executors.newCachedThreadPool();
    List<Callable<Pair<byte[], Attachment>>> listOfCallable = new ArrayList<Callable<Pair<byte[], Attachment>>>();

    for (Attachment attachment : attachments) {
      listOfCallable.add(() -> new ImmutablePair<>(GetBlobByte(attachment.getLink()), attachment));
    }
    
    String zipfilename = taskId + "_" + certificationTypeId + "_" + projectType + ".zip";

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
        } catch (Exception e1) {
          logger.error(e1.getMessage());
          throw new IllegalStateException(e1);
        }
      });
    } catch (Exception e1) {// thread was interrupted
        logger.error(e1.getMessage());
        throw new IllegalStateException(e1);
    } finally {
        // shut down the executor manually
        executor.shutdown();
    }

    FileOutputStream fos;
    ZipOutputStream zipOut;
    try {
      fos = new FileOutputStream(zipfilename);
      zipOut = new ZipOutputStream(fos);
    } catch (FileNotFoundException e1) {
        e1.printStackTrace(System.out);
        throw new IllegalStateException(e1);
    }

    File zipDir = new File(rootDir);
    try {
      zipFile(zipDir, zipDir.getName(), zipOut);
      zipOut.close();
      fos.close();
    } catch (IOException e1) {
        logger.error(e1.getMessage());
        throw new IllegalStateException(e1);
    }

    String archivedVar = "finish_archived_" + projectType;
    ProcessEngine processEngine = BpmPlatform.getDefaultProcessEngine();
    TaskService taskService = processEngine.getTaskService();
    taskService.setVariable(task.getId(), archivedVar, "finished");

    return CompletableFuture.completedFuture(true);
  }

  protected byte[] GetBlobByte(String pathname) {
    if (pathname == null) {
      return new byte[0];
    }
    
    return cloudStorage.ReadAllByte(pathname);
  }
}
