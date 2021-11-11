package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Attachment;
import com.sbh.bpm.repository.AttachmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService implements IAttachmentService {
  @Autowired
  private AttachmentRepository repository;

  @Override
  public List<Attachment> findAll() {
    return (List<Attachment>) repository.findAll();
  }

  @Override
  public Attachment findById(Integer attachmentId) {
    return repository.findById(attachmentId).get();
  }

  @Override
  public Attachment save(Attachment attachment) {
    return repository.save(attachment);
  }

  @Override
  public Iterable<Attachment> saveAll(List<Attachment> attachments) {
    return repository.saveAll(attachments);
  }

  @Override
  public List<Attachment> findByDocumentFileID(Integer documentFileId) {
    return repository.findByDocumentFileID(documentFileId);
  }

  @Override
  public List<Attachment> findByDocumentFileIDIn(List<Integer> documentFileIds) {
    return (List<Attachment>) repository.findByDocumentFileIDIn(documentFileIds);
  }
}
