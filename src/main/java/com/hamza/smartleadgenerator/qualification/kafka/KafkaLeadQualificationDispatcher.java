package com.hamza.smartleadgenerator.qualification.kafka;


import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.qualification.LeadQualificationDispatcher;
import com.hamza.smartleadgenerator.qualification.LeadQualificationJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class KafkaLeadQualificationDispatcher implements LeadQualificationDispatcher {
    private static final Logger log = LoggerFactory.getLogger(KafkaLeadQualificationListener.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaLeadQualificationDispatcher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.lead-qualification-topic}") String topic){
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void dispatch(InboundMessage message) {
        LeadQualificationJob job = new LeadQualificationJob(message.id());
        kafkaTemplate.send(topic, String.valueOf(message.id()), job)
                .whenComplete((result, ex) -> {
                    if (ex == null){
                        log.info("Sent lead qualification job for message {} to partition {} at offset {}",
                                job.messageId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                    else {
                        log.error("Failed to send lead qualification job for message {}", job.messageId(), ex);
                    }
                });
    }
}
