package com.sbh.bpm.service;

import com.sbh.bpm.service.TransactionFetchService.TransactionFetchResponse;

public interface ITransactionFetchService {
  TransactionFetchResponse getDRTransactionForProcessInstance(String processInstanceID);

}
