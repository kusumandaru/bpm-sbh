package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.DocumentFile;
import com.sbh.bpm.repository.DocumentFileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentFileService implements IDocumentFileService {
  @Autowired
  private DocumentFileRepository repository;

  @Override
  public List<DocumentFile> findAll() {
    return (List<DocumentFile>) repository.findAll();
  }

  @Override
  public DocumentFile findById(Integer documentFileId) {
    return repository.findById(documentFileId).get();
  }

  @Override
  public DocumentFile save(DocumentFile documentFile) {
    return repository.save(documentFile);
  }

  @Override
  public Iterable<DocumentFile> saveAll(List<DocumentFile> documentFiles) {
    return repository.saveAll(documentFiles);
  }

  @Override
  public List<DocumentFile> findByCriteriaScoringID(Integer criteriaScoringId) {
    return repository.findByCriteriaScoringID(criteriaScoringId);
  }

  @Override
  public List<DocumentFile> findByCriteriaScoringIDIn(List<Integer> criteriaScoringIds) {
    return (List<DocumentFile>) repository.findByCriteriaScoringIDIn(criteriaScoringIds);
  }
}
