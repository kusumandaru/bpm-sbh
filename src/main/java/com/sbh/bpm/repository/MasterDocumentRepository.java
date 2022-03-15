package com.sbh.bpm.repository;

import java.util.List;

import com.sbh.bpm.model.MasterDocument;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterDocumentRepository extends CrudRepository<MasterDocument, Integer> {
  List<MasterDocument> findBymasterCriteriaID(Integer criteriaId);
  List<MasterDocument> findBymasterCriteriaIDAndActiveTrue(Integer criteriaId);
}

