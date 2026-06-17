package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.qualification.LeadQualificationJob;
import com.hamza.smartleadgenerator.qualification.sqs.SqsLeadQualificationDispatcher;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SqsLeadQualificationDispatcherTest {
    @Mock
    private SqsTemplate sqsTemplate;
    private SqsLeadQualificationDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new SqsLeadQualificationDispatcher(
                sqsTemplate,
                "smartlead-lead-qualification"
        );
    }

    @Test
    void dispatchSendsLeadQualificationJobToSqs() {
        // Given
        InboundMessage message = new InboundMessage(
                10L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        // When
        dispatcher.dispatch(message);

        // Then
        verify(sqsTemplate).send(
                eq("smartlead-lead-qualification"),
                eq(new LeadQualificationJob(10L))
        );
    }
}
