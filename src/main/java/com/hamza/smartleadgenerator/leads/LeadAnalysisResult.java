package com.hamza.smartleadgenerator.leads;

public record LeadAnalysisResult(
        boolean qualified,
        String title,
        LeadType type,
        UrgencyLevel urgencyLevel,
        String summary
) { }
