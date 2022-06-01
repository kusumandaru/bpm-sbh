package com.sbh.bpm.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.cloud.storage.Blob;
import com.sbh.bpm.model.Attachment;
import com.sbh.bpm.repository.AttachmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService implements IAttachmentService {
  private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

  @Autowired
  private AttachmentRepository repository;

  @Autowired
  private IGoogleCloudStorage cloudStorage;

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

  @Override
  public boolean deleteById( Integer attachmentId) {
    Attachment attachment = findById(attachmentId);
    if (!Objects.isNull(attachment.getSubmittedAt())) {
      return false;
    }
    
    String attachmentLink = attachment.getLink();
    
    try {
      cloudStorage.InitCloudStorage();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    Blob blob = cloudStorage.GetBlobByName(attachmentLink);
    if (blob != null) {
      cloudStorage.DeleteBlob(blob);
    }

    repository.deleteById(attachmentId);
    return !repository.existsById(attachmentId);
  }

  @Override
  public boolean existsAttachmentByFilenameAndDocumentFileID(String fileName, Integer documentId) {
    return repository.existsAttachmentByFilenameAndDocumentFileID(fileName, documentId);
  }

  @Override
  public List<Attachment> findByProcessInstanceIdAndMasterTemplateId(String processInstanceId, Integer masterTemplateId) {
    return repository.findByProcessInstanceIdAndMasterTemplateId(processInstanceId, masterTemplateId);
  }

  @Override
  public List<Attachment> findByProcessInstanceIdAndAssessmentType(String processInstanceId, String assessmentType) {
    return repository.findByProcessInstanceIdAndAssessmentType(processInstanceId, assessmentType);
  }
}
