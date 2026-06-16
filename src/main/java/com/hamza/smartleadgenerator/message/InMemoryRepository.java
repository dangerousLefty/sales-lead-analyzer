package com.hamza.smartleadgenerator.message;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryRepository implements MessageRepository {

    private final List<InboundMessage> messages = new ArrayList<>();

    @Override
    public InboundMessage save(InboundMessage message) {
        messages.add(message);
        return message;
    }

    @Override
    public List<InboundMessage> findAll() {
        return messages;
    }

    @Override
    public Optional<InboundMessage> findById(Long id) {
        return messages.stream()
                .filter(m -> m.id().equals(id))
                .findFirst();
    }
}
