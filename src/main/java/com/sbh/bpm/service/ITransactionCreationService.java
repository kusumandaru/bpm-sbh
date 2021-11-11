package com.sbh.bpm.service;

import com.sbh.bpm.service.TransactionCreationService.TransactionCreationResponse;

public interface ITransactionCreationService {
  TransactionCreationResponse createDRTransactionForProcessInstance(String processInstanceID);
}
