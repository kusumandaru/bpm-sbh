package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.sbh.bpm.model.ProjectAttachment;
import com.sbh.bpm.model.User;
import com.sbh.bpm.service.IGoogleCloudStorage;
import com.sbh.bpm.service.IProjectAttachmentService;
import com.sbh.bpm.service.IUserService;

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

  @Autowired
  private IUserService userService;

  @Autowired
  private IGoogleCloudStorage cloudStorage;

  protected Pair<String, String> UploadToGcs(
    String directory,
    InputStream file, 
    FormDataContentDisposition fileFdcd, 
    String alias
  ) throws IOException {
    if (fileFdcd != null && fileFdcd.getFileName() != null) {
      String ext = FilenameUtils.getExtension(fileFdcd.getFileName());
      String fileName = alias + "." + ext;

      cloudStorage.SaveObject(directory, fileName, file);

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

      BlobId blobId = cloudStorage.SaveObject(activityInstanceId, fileName, file);
      runtimeService.setVariable(processInstanceId, alias, fileName);

      Pair<String, BlobId> variables = new ImmutablePair<>(alias, blobId);
      return variables;
    } else {
      return null;
    }
  }

  protected BlobId UploadToGcs(
    String directory,
    InputStream file, 
    String filename
  ) throws IOException {
    if (filename != null) {
      BlobId blobId = cloudStorage.SaveObject(directory, filename, file);

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
  
        BlobId blobId = cloudStorage.SaveObject(activityInstanceId, fileName, bytes);
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
      Blob blob = GetBlob(variableMap, directory, filename);
      if (blob == null) {
        blob = GetBlob(variableMap, filename);
      }
  
      if (blob != null) {
        cloudStorage.SetGcsSignUrl(blob);
        String publicUrl = cloudStorage.GetSignedUrl();
        Pair<String, String> variables = new ImmutablePair<>(filename, publicUrl);
        return variables;
      }
    }

    return null;
  }

  protected String GetUrlGcs (String pathname) throws IOException {
    // Get it by blob name
    Blob blob = GetBlob(pathname);

    if (blob != null) {
      cloudStorage.SetGcsSignUrl(blob);
      String publicUrl = cloudStorage.GetSignedUrl();
      return publicUrl;
    }
  
    return null;
  }

  protected Blob GetBlobDirect(Map<String, Object> variableMap, String directory, String filename) {
    Blob blob = GetBlob(variableMap, directory, filename);
    if (blob == null) {
      blob = GetBlob(variableMap, filename);
    }

    return blob;
  }

  protected Blob GetBlob(Map<String, Object> variableMap, String directory, String filename) {
    String path = String.valueOf(variableMap.get(filename));
    return cloudStorage.GetBlobByName(directory + "/" + path);
  } 

  protected Blob GetBlob(Map<String, Object> variableMap, String filename) {
    String path = String.valueOf(variableMap.get(filename));
    return cloudStorage.GetBlobByName(path);
  }

  protected Blob GetBlob(String pathname) {
    return cloudStorage.GetBlobByName(pathname);
  }

  protected byte[] GetBlobByte(String pathname) {
    return cloudStorage.ReadAllByte(pathname);
  }

  protected boolean DeleteBlob(Blob blob) {
    return cloudStorage.DeleteBlob(blob);
  }

  protected ProjectAttachment SaveWithVersion(String processInstanceId, String activityInstanceId, 
            InputStream is, ContentDisposition meta, String fileType, String username, String role) throws IOException {
    if (meta.getFileName() == null) {
      return null;
    }
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

  protected boolean SaveAvatar(InputStream is, ContentDisposition meta, User user) throws IOException {
    if (meta.getFileName() == null) {
      return false;
    }

    String filename = meta.getFileName().replaceAll(" ", "_").toLowerCase();
    String ext = FilenameUtils.getExtension(filename);
    String name = FilenameUtils.getBaseName(filename);

    String blobFilename = name + ext;
    BlobId blobId = UploadToGcs("avatar/" + user.getId(), is, blobFilename);

    user.setAvatarUrl(blobId.getName());
    user = userService.Save(user);
    return true;
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
