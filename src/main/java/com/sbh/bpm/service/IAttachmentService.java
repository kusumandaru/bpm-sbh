package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Attachment;

public interface IAttachmentService {

  List<Attachment> findAll();
  Attachment findById(Integer attachmentId);
  Attachment save(Attachment attachment);
  Iterable<Attachment> saveAll(List<Attachment> attachments);
  List<Attachment> findByDocumentFileID(Integer documentFileId);
  List<Attachment> findByDocumentFileIDIn(List<Integer> documentFileIds);
  boolean deleteById(GoogleCloudStorage googleCloudStorage, Integer attachmentId);
}
