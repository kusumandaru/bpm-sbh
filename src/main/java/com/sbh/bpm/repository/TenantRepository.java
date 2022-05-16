package com.sbh.bpm.repository;


import com.sbh.bpm.model.Tenant;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, String> {
}
