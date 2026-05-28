package com.hamza.smartleadgenerator.leads;

public record AiLeadAnalysisResponse(
        boolean qualified,
        String title,
        String type,
        String urgency,
        String summary
) { }
