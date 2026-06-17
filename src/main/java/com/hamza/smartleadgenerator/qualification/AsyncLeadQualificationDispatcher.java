package com.hamza.smartleadgenerator.qualification;

import com.hamza.smartleadgenerator.leads.LeadQualificationService;
import com.hamza.smartleadgenerator.message.InboundMessage;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("local")
public class AsyncLeadQualificationDispatcher implements LeadQualificationDispatcher{

    private final LeadQualificationService leadQualificationService;

    public AsyncLeadQualificationDispatcher(LeadQualificationService leadQualificationService) {
        this.leadQualificationService = leadQualificationService;
    }


    @Async
    @Override
    public void dispatch(InboundMessage message) {
        leadQualificationService.qualifyMessage(message);
    }
}
