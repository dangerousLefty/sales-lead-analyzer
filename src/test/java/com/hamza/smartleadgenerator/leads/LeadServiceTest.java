package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.exceptions.LeadNotFoundException;
import com.hamza.smartleadgenerator.message.InboundMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private LeadService leadService;

    @Test
    void getLeadsSuccess() {
        //Given
        Lead lead = new Lead(
                1L, 10L,
                "Test title", LeadType.OTHER, UrgencyLevel.MEDIUM, "Test summary",
                LocalDateTime.now()
        );
        when(leadRepository.findAll()).thenReturn(List.of(lead));
        // When
        List<LeadResponse> result = leadService.getLeads();
        //Then
        LeadResponse response = result.get(0);

        assertEquals(1, result.size());
        assertEquals(1L, response.id());
        assertEquals(10L, response.messageId());
        assertEquals("Test title", response.title());
        assertEquals(LeadType.OTHER, response.type());
        assertEquals(UrgencyLevel.MEDIUM, response.urgencyLevel());
        assertEquals("Test summary", response.summary());

        verify(leadRepository).findAll();
    }

    @Test
    void getSingleLeadSuccess() {
        //Given
        Lead leadTest = new Lead(
                1L, 10L,
                "Test title", LeadType.OTHER, UrgencyLevel.MEDIUM, "Test summary",
                LocalDateTime.now()
        );
        when(leadRepository.findById(leadTest.id())).thenReturn(Optional.of(leadTest));
        // When
        LeadResponse response = leadService.getLead(leadTest.id());
        //Then
        assertEquals(1L, response.id());
        assertEquals(10L, response.messageId());
        assertEquals("Test title", response.title());
        assertEquals(LeadType.OTHER, response.type());
        assertEquals(UrgencyLevel.MEDIUM, response.urgencyLevel());
        assertEquals("Test summary", response.summary());
        assertEquals(leadTest.createdAt(), response.createdAt());

        verify(leadRepository).findById(leadTest.id());
    }

    @Test
    void getSingleLeadNotFound() {
        //Given
        Long leadId = 99L;

        when(leadRepository.findById(leadId)).thenReturn(Optional.empty());
        // When
        LeadNotFoundException exception = assertThrows(
                LeadNotFoundException.class,
                () -> leadService.getLead(leadId)
        );

        assertEquals("Lead with id 99 was not found", exception.getMessage());
        verify(leadRepository).findById(leadId);
    }

    @Test
    void createLeadFromMessageTest() {
        // Given
        InboundMessage message = new InboundMessage(
                10L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        LeadAnalysisResult analysisResult = new LeadAnalysisResult(
                true,
                "Pricing inquiry",
                LeadType.PRICING_INQUIRY,
                UrgencyLevel.HIGH,
                "Customer is asking for pricing details."
        );

        when(leadRepository.save(any(Lead.class)))
                .thenAnswer(invocation -> {
                    Lead lead = invocation.getArgument(0);

                    return new Lead(
                            1L,
                            lead.messageId(),
                            lead.title(),
                            lead.type(),
                            lead.urgencyLevel(),
                            lead.summary(),
                            lead.createdAt()
                    );
                });
        // When
        LeadResponse response = leadService.createLeadFromMessage(message, analysisResult);

        //Then
        assertNull(response.id());
        assertEquals(10L, response.messageId());
        assertEquals("Pricing inquiry", response.title());
        assertEquals(LeadType.PRICING_INQUIRY, response.type());
        assertEquals(UrgencyLevel.HIGH, response.urgencyLevel());
        assertEquals("Customer is asking for pricing details.", response.summary());

        verify(leadRepository).save(any(Lead.class));
    }
}