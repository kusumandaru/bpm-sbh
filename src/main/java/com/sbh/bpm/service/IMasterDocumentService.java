package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterDocument;

public interface IMasterDocumentService {

  List<MasterDocument> findAll();
  List<MasterDocument> findBymasterCriteriaID(Integer criteriaId);
  List<MasterDocument> findBymasterCriteriaIDAndActiveTrue(Integer criteriaId);
  MasterDocument findById(Integer masterDocumentId);
  MasterDocument save(MasterDocument masterDocument);
  boolean deleteById(Integer documentId);
}
