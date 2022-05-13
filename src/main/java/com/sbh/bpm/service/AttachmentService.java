package com.sbh.bpm.service;

import java.util.List;
import java.util.Objects;

import com.google.cloud.storage.Blob;
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

  @Override
  public boolean deleteById(GoogleCloudStorage googleCloudStorage, Integer attachmentId) {
    Attachment attachment = findById(attachmentId);
    if (!Objects.isNull(attachment.getSubmittedAt())) {
      return false;
    }
    
    String attachmentLink = attachment.getLink();
    Blob blob = googleCloudStorage.GetBlobByName(attachmentLink);
    if (blob != null) {
      googleCloudStorage.DeleteBlob(blob);
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
