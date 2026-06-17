package com.hamza.smartleadgenerator.qualification.sqs;

import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.qualification.LeadQualificationDispatcher;
import com.hamza.smartleadgenerator.qualification.LeadQualificationJob;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("sqs")
public class SqsLeadQualificationDispatcher implements LeadQualificationDispatcher {

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
    }
}
