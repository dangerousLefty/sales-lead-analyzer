package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.message.InboundMessage;

public interface LeadAnalyzer {
    public LeadAnalysisResult analyze(InboundMessage message);
}
