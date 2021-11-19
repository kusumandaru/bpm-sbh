package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterLevel;

public interface IMasterLevelService {

  List<MasterLevel> findAll();
  MasterLevel findById(Integer masterLevelId);
  MasterLevel save(MasterLevel masterLevel);
}
