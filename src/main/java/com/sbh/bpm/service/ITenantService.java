package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Tenant;

public interface ITenantService {
  List<Tenant> findAll();
}
