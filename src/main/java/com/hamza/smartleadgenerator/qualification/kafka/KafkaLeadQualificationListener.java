package com.hamza.smartleadgenerator.qualification.kafka;

import com.hamza.smartleadgenerator.exceptions.MessageNotFoundException;
import com.hamza.smartleadgenerator.leads.LeadQualificationService;
import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.message.MessageRepository;
import com.hamza.smartleadgenerator.qualification.LeadQualificationJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class KafkaLeadQualificationListener {
    private static final Logger log = LoggerFactory.getLogger(KafkaLeadQualificationListener.class);
    private final LeadQualificationService leadQualificationService;
    private final MessageRepository messageRepository;

    public KafkaLeadQualificationListener(
            LeadQualificationService leadQualificationService,
            MessageRepository messageRepository
    ) {
        this.leadQualificationService = leadQualificationService;
        this.messageRepository = messageRepository;
    }

    @KafkaListener(
            topics = "${app.kafka.lead-qualification-topic}",
            groupId = "smart-lead-generator")
    public void receive(LeadQualificationJob job,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset){
        log.info("Received lead qualification job for message {} from partition {} at offset {}",
                job.messageId(), partition, offset);

        //for error simulation
//        if (job.messageId() % 2 == 0) {
//            log.info("Simulated qualification failure for message " + job.messageId());
//            throw new RuntimeException("Simulated qualification failure for message " + job.messageId());
//        }

        InboundMessage message = messageRepository.findById(job.messageId())
                .orElseThrow(() -> new MessageNotFoundException(job.messageId()));

        leadQualificationService.qualifyMessage(message);
    }
}
