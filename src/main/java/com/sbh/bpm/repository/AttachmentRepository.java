package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.Attachment;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AttachmentRepository extends CrudRepository<Attachment, Integer> {
  List<Attachment> findByDocumentFileID(Integer documentFileId);
  List<Attachment> findByDocumentFileIDIn(List<Integer> documentFileIds);
}

