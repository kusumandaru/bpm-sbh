package com.sbh.bpm.service;

import java.util.List;

import com.google.cloud.storage.Blob;
import com.sbh.bpm.model.ProjectAttachment;
import com.sbh.bpm.repository.ProjectAttachmentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectAttachmentService implements IProjectAttachmentService {
  @Autowired
  private ProjectAttachmentRepository repository;

  @Autowired
  private IGoogleCloudStorage cloudStorage;

  @Override
  public List<ProjectAttachment> findAll() {
    return (List<ProjectAttachment>) repository.findAll();
  }

  @Override
  public ProjectAttachment findById(Integer projectAttachmentId) {
    return repository.findById(projectAttachmentId).get();
  }

  @Override
  public ProjectAttachment save(ProjectAttachment projectAttachment) {
    return repository.save(projectAttachment);
  }

  @Override
  public List<ProjectAttachment> findByProcessInstanceID(String processInstanceId) {
    return repository.findByProcessInstanceID(processInstanceId);
  }

  @Override
  public ProjectAttachment findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(String processInstanceId, String filetype) {
    return repository.findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(processInstanceId, filetype);
  }

  @Override
  public ProjectAttachment saveWithVersion(ProjectAttachment projectAttachment, String processInstanceId) {
    ProjectAttachment lastProjectAttachment = findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(processInstanceId, projectAttachment.getFileType());
    Integer version = lastProjectAttachment != null ? lastProjectAttachment.getVersion() : 0;

    projectAttachment.setVersion(version + 1);
    return save(projectAttachment);
  }

  @Override
  public ProjectAttachment findByProcessInstanceIDAndId(String processInstanceId, Integer attachmentId) {
    return repository.findByProcessInstanceIDAndId(processInstanceId, attachmentId);
  }

  @Override
  public List<ProjectAttachment> findByProcessInstanceIDAndFileType(String processInstanceId, String fileType) {
    return repository.findByProcessInstanceIDAndFileType(processInstanceId, fileType);
  }

  @Override
  public boolean deleteById(Integer attachmentId) {
    ProjectAttachment attachment = findById(attachmentId);

    String attachmentLink = attachment.getLink();
    Blob blob = cloudStorage.GetBlobByName(attachmentLink);
    if (blob != null) {
      cloudStorage.DeleteBlob(blob);
    }

    repository.deleteById(attachmentId);
    return !repository.existsById(attachmentId);
  }

}
