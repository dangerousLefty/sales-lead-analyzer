package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.message.InboundMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeadQualificationServiceTest {

    @Mock
    private LeadAnalyzer leadAnalyzer;

    @Mock
    private LeadService leadService;

    @InjectMocks
    private LeadQualificationService leadQualificationService;

    @Test
    void qualifyMessageSuccess() {
        // Given
        InboundMessage message = new InboundMessage(
                1L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        LeadAnalysisResult result = new LeadAnalysisResult(
                true,
                "Pricing inquiry",
                LeadType.PRICING_INQUIRY,
                UrgencyLevel.HIGH,
                "Customer wants pricing details."
        );

        when(leadAnalyzer.analyze(message)).thenReturn(result);

        //When
        leadQualificationService.qualifyMessage(message);

        //Then
        verify(leadAnalyzer).analyze(message);
        verify(leadService).createLeadFromMessage(message, result);
    }

    @Test
    void qualifyMessageFailure(){
        //Given
        InboundMessage message = new InboundMessage(
                1L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        LeadAnalysisResult result = new LeadAnalysisResult(
                false,
                null,
                null,
                null,
                null
        );

        when(leadAnalyzer.analyze(message)).thenReturn(result);

        //When
        leadQualificationService.qualifyMessage(message);

        //Then
        verify(leadAnalyzer).analyze(message);
        verifyNoInteractions(leadService);
    }
}
