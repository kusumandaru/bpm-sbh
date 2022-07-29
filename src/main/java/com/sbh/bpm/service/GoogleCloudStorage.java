package com.sbh.bpm.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Blob.BlobSourceOption;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleCloudStorage implements IGoogleCloudStorage {
  private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStorage.class);
  
  @Value("${gcs.json-file}")
  String gcsJsonFile;

  @Value("${gcs.project}")
  String gcsProject;

  @Value("${gcs.bucket}")
  String bucketName;
  
  private Storage storage;
  private Bucket bucket;

  @Autowired
  private IGCSSignUrl gcsSignUrl;

  public void InitCloudStorage() throws IOException{
    // Use this variation to read the Google authorization JSON from the resources directory with a path
    // and a project name.
    try {
      URL url = Resources.getResource(gcsJsonFile);
      initGoogleCloudStorage(url.getPath(), gcsProject);
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  
    // Bucket require globally unique names, so you'll probably need to change this
    bucket = getBucket(bucketName);
  }
  
  
  // Use path and project name
  private void initGoogleCloudStorage(String pathToConfig, String projectId) throws FileNotFoundException, IOException {
    if (storage == null) {
      Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(pathToConfig));
      storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
    }
  }
  
  // Check for bucket existence and create if needed.
  private Bucket getBucket(String bucketName) {
      bucket = storage.get(bucketName);
      if (bucket == null) {
          System.out.println("Creating new bucket.");
          bucket = storage.create(BucketInfo.of(bucketName));
      }
      return bucket;
  }
  
  // Save a string to a blob
  public BlobId SaveObject(String directory, String blobName, InputStream stream) {
    byte[] targetArray = new byte[0];
    try {
      InitCloudStorage();
      targetArray = ByteStreams.toByteArray(stream);
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    Blob blob = bucket.create(directory + "/" + blobName, targetArray);
    return blob.getBlobId();
  }

  // Save a byte array to a blob
  public BlobId SaveObject(String directory, String blobName, byte[] targetArray) {
    try {
      InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    Blob blob = bucket.create(directory + "/" + blobName, targetArray);
    return blob.getBlobId();
  }
  
  // get a blob by id
  public byte[] GetContent(BlobId blobId) {
    try {
      InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    Blob blob = storage.get(blobId);
    return blob.getContent();
  }
  
  // get a blob by name
  public Blob GetBlobByName(String name) {
    try {
      InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }

    Blob blob = bucket.get(name);
    return blob;
    // Page<Blob> blobs = bucket.list();
    // for (Blob blob: blobs.getValues()) {
    //     if (name.equals(blob.getName())) {
    //         return blob;
    //     }
    // }
    // return null;
  }

  // delete blob
  public boolean DeleteBlob(Blob blob) {
    return blob.delete(BlobSourceOption.generationMatch());
  }

  public void SetGcsSignUrl(Blob blob) {
    gcsSignUrl.SetBucket(blob.getBucket());
    gcsSignUrl.SetBlobName(blob.getName());
  }

  public String GetSignedUrl() {
    return gcsSignUrl.GetSignedUrl();
  }
  
  // Update a blob
  public void UpdateObject(BlobId blobId, byte[] object) throws IOException {
    try {
      InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    Blob blob = storage.get(blobId);
    if (blob != null) {
        WritableByteChannel channel = blob.writer();
        channel.write(ByteBuffer.wrap(object));
        channel.close();
    }
  }


  @Override
  public byte[] ReadAllByte(String name) {
    try {
      InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }

    return storage.readAllBytes(bucketName, name);
  }
}


