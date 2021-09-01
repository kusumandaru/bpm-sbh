package com.sbh.bpm.service;

import java.time.Year;
import java.util.Calendar;
import java.util.List;

import com.sbh.bpm.model.SequenceNumber;
import com.sbh.bpm.repository.SequenceNumberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SequenceNumberService implements ISequenceNumberService {
  @Autowired
  private SequenceNumberRepository repository;

  @Override
  public List<SequenceNumber> findAll() {
    return (List<SequenceNumber>) repository.findAll();
  }
  
  @Override
  public SequenceNumber findById(Integer sequenceNumberId) {
    return repository.findById(sequenceNumberId).get();
  }

  @Override
  public SequenceNumber findByCode(String code) {
    return repository.findByCode(code);
  }

  @Override
  public SequenceNumber save(SequenceNumber buildingType) {
    return repository.save(buildingType);
  }

  @Override
  public SequenceNumber findByCodeAndYear(String code, Integer year) {
    return repository.findByCodeAndYear(code, year);
  }

  @Override
  public String getCurrentNumber(String code) {
    int year = Year.now().getValue();
    SequenceNumber sequenceNumber  = findByCodeAndYear(code, year);

    return formatNumber(sequenceNumber);
  }

  @Override
  // @Lock(LockModeType.PESSIMISTIC_WRITE)
  public String getNextNumber(String code) {
    int year = Year.now().getValue();
    SequenceNumber sequenceNumber  = findByCodeAndYear(code, year);

    return nextSequenceNumber(sequenceNumber);
  }

  private String formatNumber(SequenceNumber sequenceNumber) {
    String firstNumber = String.format("%3s", String.valueOf(sequenceNumber.getSequence())).replace(' ', '0');
    int year = Year.now().getValue();
    int month = Calendar.getInstance().get(Calendar.MONTH);

    String formatNumber = firstNumber + "/PT.SBH/" + sequenceNumber.getCode() + "/" + String.valueOf(month) + "/" + String.valueOf(year);
    return formatNumber;
  }

  private String nextSequenceNumber(SequenceNumber sequenceNumber) {
    sequenceNumber.setSequence(sequenceNumber.getSequence() + 1);
    sequenceNumber = save(sequenceNumber);
    return formatNumber(sequenceNumber);
  }
}
