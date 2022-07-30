package com.sbh.bpm.service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipOutputStream;

import org.camunda.bpm.engine.task.Task;

public interface IZipService {
  void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException;
  CompletableFuture<Boolean> CreateProjectAttachmentArchived(Task task, Integer certificationTypeId, String projectType) throws Exception;
}