package com.hamza.smartleadgenerator.ai;

import org.apache.logging.log4j.message.Message;

import java.util.List;

public record HuggingFaceRequest(
        List<Message> messages,
        String model,
        boolean stream
) {
    public record Message(
            String role,
            String content
    ){}
}
