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

  @Transactional
  @Modifying
  @Query("DELETE from Attachment a where a.id = :id")
  void delete(@Param("id") Integer id);
}

