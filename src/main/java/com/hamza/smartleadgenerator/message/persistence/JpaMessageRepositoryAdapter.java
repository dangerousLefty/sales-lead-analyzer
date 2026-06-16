package com.hamza.smartleadgenerator.message.persistence;

import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.message.MessageRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class JpaMessageRepositoryAdapter implements MessageRepository {

    private final JpaInboundMessageRepository jpaRepository;

    public JpaMessageRepositoryAdapter(JpaInboundMessageRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public InboundMessage save(InboundMessage message) {
        InboundMessageEntity entity = toEntity(message);
        jpaRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<InboundMessage> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(entity -> toDomain(entity))
                .toList();
    }

    @Override
    public Optional<InboundMessage> findById(Long id) {
        return jpaRepository.findById(id)
                .map(entity -> toDomain(entity));
    }

    private InboundMessageEntity toEntity(InboundMessage message) {
        return new InboundMessageEntity(
                message.id(),
                message.content(),
                message.createdAt()
        );
    }

    private InboundMessage toDomain(InboundMessageEntity entity) {
        return new InboundMessage(
                entity.getId(),
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
