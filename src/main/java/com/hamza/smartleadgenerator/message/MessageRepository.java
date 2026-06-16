package com.hamza.smartleadgenerator.message;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    InboundMessage save(InboundMessage message);
    List<InboundMessage> findAll();
    Optional<InboundMessage> findById(Long id);
}
