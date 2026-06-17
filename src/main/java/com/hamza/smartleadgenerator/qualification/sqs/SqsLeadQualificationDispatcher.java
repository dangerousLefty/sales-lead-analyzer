package com.hamza.smartleadgenerator.qualification.sqs;

import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.qualification.LeadQualificationDispatcher;
import com.hamza.smartleadgenerator.qualification.LeadQualificationJob;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("sqs")
public class SqsLeadQualificationDispatcher implements LeadQualificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(SqsLeadQualificationDispatcher.class);

    private final SqsTemplate sqsTemplate;
    private final String queueName;

    public SqsLeadQualificationDispatcher(
            SqsTemplate sqsTemplate,
            @Value("${app.sqs.lead-qualification-queue}") String queueName
    ) {
        this.sqsTemplate = sqsTemplate;
        this.queueName = queueName;
    }

    @Override
    public void dispatch(InboundMessage message) {
        LeadQualificationJob job = new LeadQualificationJob(message.id());

        sqsTemplate.send(queueName, job);
        log.info("Sent lead qualification job for message {} to queue {}", message.id(), queueName);
    }
}
