package com.sbh.bpm.repository;

import com.sbh.bpm.model.MasterVendor;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MasterVendorRepository extends CrudRepository<MasterVendor, Integer> {
}

