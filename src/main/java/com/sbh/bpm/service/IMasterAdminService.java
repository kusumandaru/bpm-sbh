package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.MasterAdmin;

public interface IMasterAdminService {

  List<MasterAdmin> findAll();
  MasterAdmin findById(Integer masterAdminId);
  MasterAdmin save(MasterAdmin masterAdmin);
}
