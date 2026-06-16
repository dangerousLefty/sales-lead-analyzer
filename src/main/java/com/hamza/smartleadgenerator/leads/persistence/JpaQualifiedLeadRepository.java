package com.hamza.smartleadgenerator.leads.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaQualifiedLeadRepository extends JpaRepository<QualifiedLeadEntity, Long> {

}
