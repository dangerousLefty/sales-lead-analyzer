package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.message.InboundMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class LeadQualificationService {
    private static final Logger log = LoggerFactory.getLogger(LeadQualificationService.class);
    private final LeadAnalyzer leadAnalyzer;
    private final LeadService leadService;


    public LeadQualificationService(LeadAnalyzer leadAnalyzer, LeadService leadService) {
        this.leadAnalyzer = leadAnalyzer;
        this.leadService = leadService;
    }

    public void qualifyMessage(InboundMessage message){
        try {
            LeadAnalysisResult result = leadAnalyzer.analyze(message);
            if (result.qualified()){
                leadService.createLeadFromMessage(message, result);
                log.info("Qualified message {} as a lead", message.id());
            }
            else {
                log.info("Message {} was not qualified as a lead", message.id());
            }
        }
        catch (Exception e){
            log.error("Failed to qualify message {}", message.id(), e);
        }

    }
}
