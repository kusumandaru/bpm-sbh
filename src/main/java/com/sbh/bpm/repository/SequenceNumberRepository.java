package com.sbh.bpm.repository;

import com.sbh.bpm.model.SequenceNumber;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SequenceNumberRepository extends CrudRepository<SequenceNumber, Integer> {
  SequenceNumber findByCode(String code);
  SequenceNumber findByCodeAndYear(String code, Integer year);
}

