package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.exceptions.LeadNotFoundException;
import com.hamza.smartleadgenerator.message.InboundMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class LeadService {
    private final LeadRepository leadRepository;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public LeadResponse createLeadFromMessage(
            InboundMessage message,
            LeadAnalysisResult result
    ){
        Lead lead = new Lead(
                idGenerator.getAndIncrement(),
                message.id(),
                result.title(),
                result.type(),
                result.urgencyLevel(),
                result.summary(),
                message.createdAt()
        );

        leadRepository.save(lead);
        return toResponse(lead);
    }

    public LeadResponse getLead(Long id){
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new LeadNotFoundException(id));

        return toResponse(lead);
    }

    public List<LeadResponse> getLeads(){
        return leadRepository.findAll()
                .stream()
                .map(lead -> toResponse(lead))
                .toList();
    }

    private LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.id(),
                lead.messageId(),
                lead.title(),
                lead.type(),
                lead.urgencyLevel(),
                lead.summary(),
                lead.createdAt()
        );
    }

}
