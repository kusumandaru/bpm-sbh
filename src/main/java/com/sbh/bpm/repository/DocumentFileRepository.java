package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.DocumentFile;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DocumentFileRepository extends CrudRepository<DocumentFile, Integer> {
  List<DocumentFile> findByCriteriaScoringID(Integer criteriaScoringId);
  List<DocumentFile> findByCriteriaScoringIDIn(List<Integer> criteriaScoringIds);
}

