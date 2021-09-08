package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.SequenceNumber;
import com.sbh.bpm.service.SequenceNumberService.NUMBER_FORMAT;

public interface ISequenceNumberService {

  List<SequenceNumber> findAll();
  SequenceNumber findById(Integer sequenceNumberId);
  SequenceNumber findByCode(String code);
  SequenceNumber findByCodeAndYear(String code, Integer year);
  SequenceNumber save(SequenceNumber sequenceNumber);
  String getCurrentNumber(String code, NUMBER_FORMAT format);
  String getNextNumber(String code, NUMBER_FORMAT format);

}
