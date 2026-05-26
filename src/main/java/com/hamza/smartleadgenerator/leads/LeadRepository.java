package com.hamza.smartleadgenerator.leads;

import java.util.List;
import java.util.Optional;

public interface LeadRepository {
    Lead save(Lead lead);
    List<Lead> findAll();
    Optional<Lead> findById(Long id);
}
