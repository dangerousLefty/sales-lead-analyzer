package com.hamza.smartleadgenerator.message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String content,
        LocalDateTime createdAt
) {}
