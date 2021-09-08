package com.sbh.bpm.service;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import com.github.fracpete.romannumerals4j.RomanNumeralFormat;
import com.sbh.bpm.model.SequenceNumber;
import com.sbh.bpm.repository.SequenceNumberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

@Service
public class SequenceNumberService implements ISequenceNumberService {
  public enum NUMBER_FORMAT {
    GENERAL, REGISTERED
  }

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
  public String getCurrentNumber(String code, NUMBER_FORMAT format) {
    int year = Year.now().getValue();
    SequenceNumber sequenceNumber = findOrCreateSequenceNumber(code, year);

    return formatNumber(sequenceNumber, format);
  }

  @Override
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  public String getNextNumber(String code, NUMBER_FORMAT format) {
    int year = Year.now().getValue();
    SequenceNumber sequenceNumber = findOrCreateSequenceNumber(code, year);

    return nextSequenceNumber(sequenceNumber, format);
  }

  private String formatNumber(SequenceNumber sequenceNumber, NUMBER_FORMAT format) {
    String runningNumber = String.format("%3s", String.valueOf(sequenceNumber.getSequence())).replace(' ', '0');
    Date date = new Date();
    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    int year  = localDate.getYear();
    int month = localDate.getMonthValue();

    RomanNumeralFormat roman = new RomanNumeralFormat();
    String letterNumber = "";
    if (format == NUMBER_FORMAT.GENERAL){
      letterNumber = runningNumber + "/PT.SBH/" + sequenceNumber.getCode() + "/" + roman.format(month) + "/" + String.valueOf(year);
    } else if (format == NUMBER_FORMAT.REGISTERED){
      letterNumber = sequenceNumber.getCode() + "/" + runningNumber +  "/" + roman.format(month) + "/" + String.valueOf(year);
    }
    return letterNumber;
  }

  private String nextSequenceNumber(SequenceNumber sequenceNumber, NUMBER_FORMAT format) {
    sequenceNumber.setSequence(sequenceNumber.getSequence() + 1);
    sequenceNumber = save(sequenceNumber);
    return formatNumber(sequenceNumber, format);
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
