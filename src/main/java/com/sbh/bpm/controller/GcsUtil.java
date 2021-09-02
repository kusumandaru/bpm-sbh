package com.sbh.bpm.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.sbh.bpm.service.GoogleCloudStorage;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.camunda.bpm.engine.RuntimeService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

class GcsUtil {
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
      String fileName = activityInstanceId + "__" + alias + "." + ext;

      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      BlobId blobId = googleCloudStorage.SaveObject(fileName, file);
      runtimeService.setVariable(processInstanceId, alias, fileName);

      Pair<String, BlobId> variables = new ImmutablePair<>(alias, blobId);
      return variables;
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
        String fileName = activityInstanceId + "__" + alias + "." + ext;
  
        GoogleCloudStorage googleCloudStorage;
        googleCloudStorage = new GoogleCloudStorage();
  
        BlobId blobId = googleCloudStorage.SaveObject(fileName, bytes);
        runtimeService.setVariable(processInstanceId, alias, fileName);
  
        Pair<String, BlobId> variables = new ImmutablePair<>(alias, blobId);
        return variables;
      } else {
        return null;
      }
}
  
  protected Pair<String, String> GetUrlGcs(Map<String, Object> variableMap, String filename) throws IOException {
    // Get it by blob name
    if (variableMap.get(filename) != null) {
      GoogleCloudStorage googleCloudStorage;
      googleCloudStorage = new GoogleCloudStorage();

      Blob blob = GetBlob(googleCloudStorage, variableMap, filename);
  
      if (blob != null) {
        googleCloudStorage.SetGcsSignUrl(blob);
        String publicUrl = googleCloudStorage.GetSignedUrl();
        Pair<String, String> variables = new ImmutablePair<>(filename, publicUrl);
        return variables;
      }
    }

    return null;
  }

  protected Blob GetBlob(GoogleCloudStorage googleCloudStorage, Map<String, Object> variableMap, String filename) {
    String path = String.valueOf(variableMap.get(filename));
    return googleCloudStorage.GetBlobByName(path);
  } 
}
