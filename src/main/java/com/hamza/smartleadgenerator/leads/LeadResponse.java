package com.hamza.smartleadgenerator.leads;

import java.time.LocalDateTime;

public record LeadResponse(
        Long id,
        Long messageId,
        String title,
        LeadType type,
        UrgencyLevel urgencyLevel,
        String summary,
        LocalDateTime createdAt
) { }
