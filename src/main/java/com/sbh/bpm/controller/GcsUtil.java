package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.sbh.bpm.model.ProjectAttachment;
import com.sbh.bpm.service.GoogleCloudStorage;
import com.sbh.bpm.service.IProjectAttachmentService;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.engine.RuntimeService;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.beans.factory.annotation.Autowired;

class GcsUtil {
  @Autowired
  private IProjectAttachmentService projectAttachmentService;
  
  protected Pair<String, String> UploadToGcs(
    String directory,
    InputStream file, 
    FormDataContentDisposition fileFdcd, 
    String alias
  ) throws IOException {
    if (fileFdcd != null && fileFdcd.getFileName() != null) {
      String ext = FilenameUtils.getExtension(fileFdcd.getFileName());
      String fileName = alias + "." + ext;

      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      googleCloudStorage.SaveObject(directory, fileName, file);

      Pair<String, String> variables = new ImmutablePair<>(alias, fileName);
      return variables;
    } else {
      return null;
    }
  }

  protected Pair<String, BlobId> UploadToGcs(
    RuntimeService runtimeService,
    String processInstanceId,
    String activityInstanceId,
    InputStream file, 
    FormDataContentDisposition fileFdcd, 
    String alias
  ) throws IOException {
    if (fileFdcd.getFileName() != null) {
      String ext = FilenameUtils.getExtension(fileFdcd.getFileName());
      String fileName = alias + "." + ext;

      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      BlobId blobId = googleCloudStorage.SaveObject(activityInstanceId, fileName, file);
      runtimeService.setVariable(processInstanceId, alias, fileName);

      Pair<String, BlobId> variables = new ImmutablePair<>(alias, blobId);
      return variables;
    } else {
      return null;
    }
  }

  protected BlobId UploadToGcs(
    String activityInstanceId,
    InputStream file, 
    String filename
  ) throws IOException {
    if (filename != null) {
      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      BlobId blobId = googleCloudStorage.SaveObject(activityInstanceId, filename, file);

      return blobId;
    } else {
      return null;
    }
  }

  protected Pair<String, BlobId> UploadToGcs(
    RuntimeService runtimeService,
    String processInstanceId,
    String activityInstanceId,
    byte[] bytes, 
    String alias,
    String ext) throws IOException {
      if (bytes != null  && bytes.length > 0) {
        String fileName = alias + "." + ext;
  
        GoogleCloudStorage googleCloudStorage;
        googleCloudStorage = new GoogleCloudStorage();
  
        BlobId blobId = googleCloudStorage.SaveObject(activityInstanceId, fileName, bytes);
        runtimeService.setVariable(processInstanceId, alias, fileName);
  
        Pair<String, BlobId> variables = new ImmutablePair<>(alias, blobId);
        return variables;
      } else {
        return null;
      }
}
  
  protected Pair<String, String> GetUrlGcs(Map<String, Object> variableMap, String directory, String filename) throws IOException {
    // Get it by blob name
    if (variableMap.get(filename) != null) {
      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      Blob blob = GetBlob(googleCloudStorage, variableMap, directory, filename);
      if (blob == null) {
        blob = GetBlob(googleCloudStorage, variableMap, filename);
      }
  
      if (blob != null) {
        googleCloudStorage.SetGcsSignUrl(blob);
        String publicUrl = googleCloudStorage.GetSignedUrl();
        Pair<String, String> variables = new ImmutablePair<>(filename, publicUrl);
        return variables;
      }
    }

    return null;
  }

  protected String GetUrlGcs (String pathname) throws IOException {
    // Get it by blob name
    GoogleCloudStorage googleCloudStorage;
    googleCloudStorage = new GoogleCloudStorage();

    Blob blob = GetBlob(googleCloudStorage, pathname);

    if (blob != null) {
      googleCloudStorage.SetGcsSignUrl(blob);
      String publicUrl = googleCloudStorage.GetSignedUrl();
      return publicUrl;
    }
  
    return null;
  }

  protected Blob GetBlobDirect(GoogleCloudStorage googleCloudStorage, Map<String, Object> variableMap, String directory, String filename) {
    Blob blob = GetBlob(googleCloudStorage, variableMap, directory, filename);
    if (blob == null) {
      blob = GetBlob(googleCloudStorage, variableMap, filename);
    }

    return blob;
  }

  protected Blob GetBlob(GoogleCloudStorage googleCloudStorage, Map<String, Object> variableMap, String directory, String filename) {
    String path = String.valueOf(variableMap.get(filename));
    return googleCloudStorage.GetBlobByName(directory + "/" + path);
  } 

  protected Blob GetBlob(GoogleCloudStorage googleCloudStorage, Map<String, Object> variableMap, String filename) {
    String path = String.valueOf(variableMap.get(filename));
    return googleCloudStorage.GetBlobByName(path);
  }

  protected Blob GetBlob(GoogleCloudStorage googleCloudStorage, String pathname) {
    return googleCloudStorage.GetBlobByName(pathname);
  }

  protected boolean DeleteBlob(GoogleCloudStorage googleCloudStorage, Blob blob) {
    return googleCloudStorage.DeleteBlob(blob);
  }

  protected ProjectAttachment SaveWithVersion(String processInstanceId, String activityInstanceId, 
            InputStream is, ContentDisposition meta, String fileType, String username) throws IOException {
    if (meta.getFileName() == null) {
      return null;
    }
    //change later
    String role = "client";
    ProjectAttachment attachment = new ProjectAttachment();

    BlobId blobID = uploadToGCSService(is, meta, activityInstanceId, fileType);

    attachment.setFileType(fileType);
    attachment.setCreatedAt(new Date());
    attachment.setRole(role);
    attachment.setUploaderID(username);

    attachment.setFilename(meta.getFileName());
    attachment.setLink(blobID.getName());
    attachment.setProcessInstanceID(processInstanceId);

    attachment = projectAttachmentService.saveWithVersion(attachment, processInstanceId);
    return attachment;
  }

  private BlobId uploadToGCSService(InputStream is, ContentDisposition meta, String activityInstanceId, String fileType) throws IOException {
    String filename = meta.getFileName().replaceAll(" ", "_").toLowerCase();
    String ext = FilenameUtils.getExtension(filename);
    String name = FilenameUtils.getBaseName(filename);

    String blobFilename = name + "_" + String.valueOf(Instant.now().toEpochMilli()) + "." + ext;
    BlobId blobId = UploadToGcs(activityInstanceId, is, blobFilename);

    return blobId;
  }
}
