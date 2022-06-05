package com.sbh.bpm.service;

import java.util.List;

import com.sbh.bpm.model.Tenant;
import com.sbh.bpm.repository.TenantRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class TenantService implements ITenantService{

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private TenantRepository tenantRepository;

  @Override
  public List<Tenant> findAll() {
    return (List<Tenant>) tenantRepository.findAll();
  }

  @Override
  public Long Count() {
    return tenantRepository.count();
  }
}
