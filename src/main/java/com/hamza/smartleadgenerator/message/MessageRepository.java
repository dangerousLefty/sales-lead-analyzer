package com.hamza.smartleadgenerator.message;

import java.util.List;

public interface MessageRepository {
    InboundMessage save(InboundMessage message);
    List<InboundMessage> findAll();
}
