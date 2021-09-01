package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.SequenceNumber;

public interface ISequenceNumberService {

  List<SequenceNumber> findAll();
  SequenceNumber findById(Integer sequenceNumberId);
  SequenceNumber findByCode(String code);
  SequenceNumber findByCodeAndYear(String code, Integer year);
  SequenceNumber save(SequenceNumber sequenceNumber);
  String getCurrentNumber(String code);
  String getNextNumber(String code);
}
