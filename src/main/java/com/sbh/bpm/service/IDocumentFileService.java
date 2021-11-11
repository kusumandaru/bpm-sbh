package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.DocumentFile;

public interface IDocumentFileService {

  List<DocumentFile> findAll();
  DocumentFile findById(Integer documentFileId);
  DocumentFile save(DocumentFile documentFile);
  Iterable<DocumentFile> saveAll(List<DocumentFile> documentFiles);
  List<DocumentFile> findByCriteriaScoringID(Integer criteriaScoringId);
  List<DocumentFile> findByCriteriaScoringIDIn(List<Integer> criteriaScoringIds);
}
