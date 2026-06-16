package com.hamza.smartleadgenerator.leads.persistence;

import com.hamza.smartleadgenerator.leads.Lead;
import com.hamza.smartleadgenerator.leads.LeadRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class JpaLeadRepositoryAdapter implements LeadRepository {
    private final JpaQualifiedLeadRepository jpaRepository;

    public JpaLeadRepositoryAdapter(JpaQualifiedLeadRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Lead save(Lead lead) {
        QualifiedLeadEntity entity = toEntity(lead);
        jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<Lead> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(entity -> toDomain(entity))
                .toList();
    }

    @Override
    public Optional<Lead> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entity -> toDomain(entity));
    }

    private QualifiedLeadEntity toEntity(Lead lead) {
        return new QualifiedLeadEntity(
                lead.id(),
                lead.messageId(),
                lead.title(),
                lead.type(),
                lead.urgencyLevel(),
                lead.summary(),
                lead.createdAt()
        );
    }

    private Lead toDomain(QualifiedLeadEntity entity) {
        return new Lead(
                entity.getId(),
                entity.getMessageId(),
                entity.getTitle(),
                entity.getType(),
                entity.getUrgency(),
                entity.getSummary(),
                entity.getCreatedAt()
        );
    }
}
