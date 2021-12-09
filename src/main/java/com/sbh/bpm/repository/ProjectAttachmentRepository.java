package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.ProjectAttachment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectAttachmentRepository extends CrudRepository<ProjectAttachment, Integer> {
  List<ProjectAttachment> findByProcessInstanceID(String processInstanceId);
  ProjectAttachment findTopByProcessInstanceIDAndFileTypeOrderByIdDesc(String processInstanceId, String filetype);
  ProjectAttachment findByProcessInstanceIDAndId(String processInstanceId, Integer attachmentId);
  List<ProjectAttachment> findByProcessInstanceIDAndFileType(String processInstanceId, String fileType);
}

