package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterDocument;
import com.sbh.bpm.repository.MasterDocumentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterDocumentService implements IMasterDocumentService {
  @Autowired
  private MasterDocumentRepository repository;

  @Override
  public List<MasterDocument> findAll() {
    return (List<MasterDocument>) repository.findAll();
  }
  
  @Override
  public MasterDocument findById(Integer masterDocumentId) {
    return repository.findById(masterDocumentId).get();
  }

  @Override
  public MasterDocument save(MasterDocument masterDocument) {
    return repository.save(masterDocument);
  }

  @Override
  public List<MasterDocument> findBymasterCriteriaID(Integer criteriaId) {
    return (List<MasterDocument>) repository.findBymasterCriteriaID(criteriaId);
  }

  @Override
  public List<MasterDocument> findBymasterCriteriaIDAndActiveTrue(Integer criteriaId) {
    return (List<MasterDocument>) repository.findBymasterCriteriaIDAndActiveTrue(criteriaId);
  }

  @Override
  public boolean deleteById(Integer documentId) {
    repository.deleteById(documentId);
    return !repository.existsById(documentId);
  }
}
