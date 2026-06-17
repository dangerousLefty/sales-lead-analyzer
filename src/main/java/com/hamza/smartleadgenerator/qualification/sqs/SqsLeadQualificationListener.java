package com.hamza.smartleadgenerator.qualification.sqs;

import com.hamza.smartleadgenerator.exceptions.MessageNotFoundException;
import com.hamza.smartleadgenerator.leads.LeadQualificationService;
import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.message.MessageRepository;
import com.hamza.smartleadgenerator.qualification.LeadQualificationJob;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqsLeadQualificationListener {
    private static final Logger log = LoggerFactory.getLogger(SqsLeadQualificationListener.class);

    private final MessageRepository messageRepository;
    private final LeadQualificationService leadQualificationService;

    public SqsLeadQualificationListener(
            MessageRepository messageRepository,
            LeadQualificationService leadQualificationService) {
        this.messageRepository = messageRepository;
        this.leadQualificationService = leadQualificationService;
    }

    @SqsListener("${app.sqs.lead-qualification-queue}")
    public void receive(LeadQualificationJob job){
        log.info("Received lead qualification job for message {}", job.messageId());

        InboundMessage message = messageRepository.findById(job.messageId())
                .orElseThrow(() -> new MessageNotFoundException(job.messageId()));

        leadQualificationService.qualifyMessage(message);
    }
}
