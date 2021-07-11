package com.sbh.bpm.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

import com.google.api.gax.paging.Page;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;

public class GoogleCloudStorage {
  private Storage storage;
  private Bucket bucket;
  private GCSSignUrl gcsSignUrl;

  public GoogleCloudStorage() throws IOException{
    // Use this variation to read the Google authorization JSON from the resources directory with a path
    // and a project name.
    try {
      URL url = Resources.getResource("bpm-sbh.json");
      initGoogleCloudStorage(url.getPath(), "bpm_sbh");
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    // Bucket require globally unique names, so you'll probably need to change this
    bucket = getBucket("bpm-sbh");
  }
  
  
  // Use path and project name
  private void initGoogleCloudStorage(String pathToConfig, String projectId) throws FileNotFoundException, IOException {
      Credentials credentials = GoogleCredentials.fromStream(new FileInputStream(pathToConfig));
      storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build().getService();
  }
  
  // Check for bucket existence and create if needed.
  public Bucket getBucket(String bucketName) {
      bucket = storage.get(bucketName);
      if (bucket == null) {
          System.out.println("Creating new bucket.");
          bucket = storage.create(BucketInfo.of(bucketName));
      }
      return bucket;
  }
  
  // Save a string to a blob
  public BlobId SaveObject(String blobName, InputStream stream) {
    byte[] targetArray = new byte[0];
    try {
      targetArray = ByteStreams.toByteArray(stream);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Blob blob = bucket.create(blobName, targetArray);
    return blob.getBlobId();
  }
  
  // get a blob by id
  public byte[] getContent(BlobId blobId) {
    Blob blob = storage.get(blobId);
    return blob.getContent();
  }
  
  // get a blob by name
  public Blob GetBlobByName(String name) {
    Page<Blob> blobs = bucket.list();
    for (Blob blob: blobs.getValues()) {
        if (name.equals(blob.getName())) {
            return blob;
        }
    }
    return null;
  }

  public GCSSignUrl SetGcsSignUrl(Blob blob) {
    gcsSignUrl = new GCSSignUrl();
    gcsSignUrl.SetBucket(blob.getBucket());
    gcsSignUrl.SetBlobName(blob.getName());

    return gcsSignUrl;
  }

  public String GetSignedUrl() {
    return gcsSignUrl.GetSignedUrl();
  }
  
  // Update a blob
  public void UpdateObject(BlobId blobId, byte[] object) throws IOException {
    Blob blob = storage.get(blobId);
    if (blob != null) {
        WritableByteChannel channel = blob.writer();
        channel.write(ByteBuffer.wrap(object));
        channel.close();
    }
  }
}


