package com.hamza.smartleadgenerator.leads;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryLeadRepository implements LeadRepository{

   private final Map<Long, Lead> leadMap = new HashMap<>();

    @Override
    public Lead save(Lead lead) {
        leadMap.put(lead.id(), lead);
        return lead;
    }

    @Override
    public List<Lead> findAll() {
        return leadMap.values()
                .stream()
                .toList();
    }

    @Override
    public Optional<Lead> findById(Long id) {
        return Optional.ofNullable(leadMap.get(id));
    }
}
