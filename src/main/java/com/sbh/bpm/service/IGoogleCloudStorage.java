package com.sbh.bpm.service;

import java.io.IOException;
import java.io.InputStream;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;

public interface IGoogleCloudStorage {
  void InitCloudStorage() throws IOException;
  BlobId SaveObject(String directory, String blobName, InputStream stream);
  BlobId SaveObject(String directory, String blobName, byte[] targetArray) ;
  byte[] GetContent(BlobId blobId);
  Blob GetBlobByName(String name);
  boolean DeleteBlob(Blob blob);
  void SetGcsSignUrl(Blob blob);
  String GetSignedUrl();
  void UpdateObject(BlobId blobId, byte[] object) throws IOException;
}
