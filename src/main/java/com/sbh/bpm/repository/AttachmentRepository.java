package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.Attachment;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Integer> {
  List<Attachment> findByDocumentFileID(Integer documentFileId);
  List<Attachment> findByDocumentFileIDIn(List<Integer> documentFileIds);
  boolean existsAttachmentByFilenameAndDocumentFileID(String filename, Integer documentFileID);

  @Transactional
  @Modifying
  @Query("DELETE from Attachment a where a.id = :id")
  void delete(@Param("id") Integer id);

  @Transactional
  @Modifying
  @Query(value="SELECT attachments.* from attachments "+
    "WHERE document_file_id IN (SELECT id from document_files "+
    "WHERE criteria_scoring_id IN (SELECT id from criteria_scorings "+
    "WHERE project_assessment_id = (SELECT id from project_assessments "+
    "WHERE process_instance_id = :processInstanceId and master_template_id = :masterTemplateId)))", nativeQuery=true)
  List<Attachment> findByProcessInstanceIdAndMasterTemplateId(@Param("processInstanceId") String processInstanceId, @Param("masterTemplateId") Integer masterTemplateId);

  @Transactional
  @Modifying
  @Query(value="SELECT attachments.* from attachments "+
    "WHERE document_file_id IN (SELECT id from document_files "+
    "WHERE criteria_scoring_id IN (SELECT id from criteria_scorings "+
    "WHERE project_assessment_id = (SELECT id from project_assessments "+
    "WHERE process_instance_id = :processInstanceId and assessment_type = :assessmentType)))", nativeQuery=true)
  List<Attachment> findByProcessInstanceIdAndAssessmentType(String processInstanceId, String assessmentType);
}

