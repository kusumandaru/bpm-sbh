package com.sbh.bpm.service;

import java.time.Year;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import javax.persistence.LockModeType;

import com.sbh.bpm.model.SequenceNumber;
import com.sbh.bpm.repository.SequenceNumberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Lock;
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
    SequenceNumber sequenceNumber = findOrCreateSequenceNumber(code, year);

    return formatNumber(sequenceNumber);
  }

  @Override
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  public String getNextNumber(String code) {
    int year = Year.now().getValue();
    SequenceNumber sequenceNumber = findOrCreateSequenceNumber(code, year);

    return nextSequenceNumber(sequenceNumber);
  }

  private String formatNumber(SequenceNumber sequenceNumber) {
    String firstNumber = String.format("%3s", String.valueOf(sequenceNumber.getSequence())).replace(' ', '0');
    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int year  = localDate.getYear();
    int month = localDate.getMonthValue();

    String formatNumber = firstNumber + "/PT.SBH/" + sequenceNumber.getCode() + "/" + String.valueOf(month) + "/" + String.valueOf(year);
    return formatNumber;
  }

  private String nextSequenceNumber(SequenceNumber sequenceNumber) {
    sequenceNumber.setSequence(sequenceNumber.getSequence() + 1);
    sequenceNumber = save(sequenceNumber);
    return formatNumber(sequenceNumber);
  }

  private SequenceNumber findOrCreateSequenceNumber(String code, Integer year) {
    SequenceNumber sequenceNumber  = findByCodeAndYear(code, year);

    if (sequenceNumber == null) {
      Date date = new Date();
  
      sequenceNumber = new SequenceNumber();
      sequenceNumber.setYear(year);
      sequenceNumber.setCode(code);
      sequenceNumber.setSequence(1);
      sequenceNumber.setCreatedAt(date);
      sequenceNumber.setUpdatedAt(date);

      sequenceNumber = save(sequenceNumber);
    }

    return sequenceNumber;
  }
}
