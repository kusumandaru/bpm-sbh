package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.ProjectAttachment;

public interface IProjectAttachmentService {

  List<ProjectAttachment> findAll();
  ProjectAttachment findById(Integer projectAttachmentId);
  ProjectAttachment save(ProjectAttachment projectAttachment);
  List<ProjectAttachment> findByProcessInstanceID(String processInstanceId);
  ProjectAttachment findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(String processInstanceId, String filetype);
  ProjectAttachment saveWithVersion(ProjectAttachment projectAttachment, String processInstanceId);
  ProjectAttachment findByProcessInstanceIDAndId(String processInstanceId, Integer attachmentId);
  List<ProjectAttachment> findByProcessInstanceIDAndFileType(String processInstanceId, String fileType);
  boolean deleteById(Integer attachmentId);
}
