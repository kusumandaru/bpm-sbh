package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterEvaluation;
import com.sbh.bpm.service.TransactionFetchService.TransactionFetchResponse;

public interface ITransactionFetchService {
  TransactionFetchResponse GetDRTransactionForProcessInstance(String processInstanceID);
  List<MasterEvaluation> GetEvaluationScoreForProcessInstance(String processInstanceID);
}
