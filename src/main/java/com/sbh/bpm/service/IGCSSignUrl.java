package com.sbh.bpm.service;

public interface IGCSSignUrl {
  public void SetBucket(String name);
  public void SetBlobName(String name);
  public String GetSignedUrl();
}

