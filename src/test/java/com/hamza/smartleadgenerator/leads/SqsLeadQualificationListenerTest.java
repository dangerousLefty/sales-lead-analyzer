package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.exceptions.MessageNotFoundException;
import com.hamza.smartleadgenerator.message.InboundMessage;
import com.hamza.smartleadgenerator.message.MessageRepository;
import com.hamza.smartleadgenerator.qualification.LeadQualificationJob;
import com.hamza.smartleadgenerator.qualification.sqs.SqsLeadQualificationListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SqsLeadQualificationListenerTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private LeadQualificationService leadQualificationService;

    @InjectMocks
    private SqsLeadQualificationListener listener;

    @Test
    void receiveProcessesExistingMessage() {
        // Given
        LeadQualificationJob job = new LeadQualificationJob(10L);

        InboundMessage message = new InboundMessage(
                10L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        when(messageRepository.findById(10L))
                .thenReturn(Optional.of(message));

        // When
        listener.receive(job);

        // Then
        verify(messageRepository).findById(10L);
        verify(leadQualificationService).qualifyMessage(message);
    }

    @Test
    void receiveThrowsExceptionWhenMessageDoesNotExist() {
        // Given
        LeadQualificationJob job = new LeadQualificationJob(999L);

        when(messageRepository.findById(999L))
                .thenReturn(Optional.empty());

        // When + Then
        assertThrows(
                MessageNotFoundException.class,
                () -> listener.receive(job)
        );

        verify(messageRepository).findById(999L);
        verifyNoInteractions(leadQualificationService);
    }


}
