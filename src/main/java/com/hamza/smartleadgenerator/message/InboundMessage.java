package com.hamza.smartleadgenerator.message;

import java.time.LocalDateTime;

public record InboundMessage(
        Long id,
        String content,
        LocalDateTime createdAt
) {}
