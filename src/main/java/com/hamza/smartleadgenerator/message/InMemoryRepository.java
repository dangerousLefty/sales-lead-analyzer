package com.hamza.smartleadgenerator.message;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

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
}
