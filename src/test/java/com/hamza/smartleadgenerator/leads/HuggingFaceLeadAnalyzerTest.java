package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.ai.ChatCompletionResponse;
import com.hamza.smartleadgenerator.ai.HuggingFaceRequest;
import com.hamza.smartleadgenerator.ai.HuggingFaceService;
import com.hamza.smartleadgenerator.exceptions.AiAnalysisException;
import com.hamza.smartleadgenerator.message.InboundMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HuggingFaceLeadAnalyzerTest {

    @Mock
    private HuggingFaceService huggingFaceService;

    private ObjectMapper objectMapper;

    private HuggingFaceLeadAnalyzer huggingFaceLeadAnalyzer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        huggingFaceLeadAnalyzer = new HuggingFaceLeadAnalyzer(
                huggingFaceService,
                objectMapper
        );
    }

    @Test
    void returnQualifiedAnalysisResult() {
        // Given
        InboundMessage message = new InboundMessage(
                1L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        ChatCompletionResponse response = new ChatCompletionResponse(
                List.of(
                        new ChatCompletionResponse.Choice(
                                new ChatCompletionResponse.Message(
                                        "assistant",
                                        """
                                        {
                                          "qualified": true,
                                          "title": "Pricing inquiry",
                                          "type": "pricing-inquiry",
                                          "urgency": "high",
                                          "summary": "Customer wants pricing details."
                                        }
                                        """
                                )
                        )
                )
        );

        when(huggingFaceService.completion(any(HuggingFaceRequest.class)))
                .thenReturn(response);

        // When
        LeadAnalysisResult result = huggingFaceLeadAnalyzer.analyze(message);

        // Then
        assertTrue(result.qualified());
        assertEquals("Pricing inquiry", result.title());
        assertEquals(LeadType.PRICING_INQUIRY, result.type());
        assertEquals(UrgencyLevel.HIGH, result.urgencyLevel());
        assertEquals("Customer wants pricing details.", result.summary());

        verify(huggingFaceService).completion(any(HuggingFaceRequest.class));
    }

    @Test
    void returnUnqualifiedAnalysisResult() {
        // Given
        InboundMessage message = new InboundMessage(
                1L,
                "Your website looks great!",
                LocalDateTime.now()
        );

        ChatCompletionResponse response = new ChatCompletionResponse(
                List.of(
                        new ChatCompletionResponse.Choice(
                                new ChatCompletionResponse.Message(
                                        "assistant",
                                        """
                                        {
                                          "qualified": false,
                                          "title": null,
                                          "type": null,
                                          "urgency": null,
                                          "summary": null
                                        }
                                        """
                                )
                        )
                )
        );

        when(huggingFaceService.completion(any(HuggingFaceRequest.class)))
                .thenReturn(response);

        // When
        LeadAnalysisResult result = huggingFaceLeadAnalyzer.analyze(message);

        // Then
        assertFalse(result.qualified());
        assertNull(result.title());
        assertNull(result.type());
        assertNull(result.urgencyLevel());
        assertNull(result.summary());

        verify(huggingFaceService).completion(any(HuggingFaceRequest.class));
    }

    @Test
    void givenInvalidAiResponse_whenAnalyze_thenThrowAiAnalysisException() {
        // Given
        InboundMessage message = new InboundMessage(
                1L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        ChatCompletionResponse response = new ChatCompletionResponse(
                List.of(
                        new ChatCompletionResponse.Choice(
                                new ChatCompletionResponse.Message(
                                        "assistant",
                                        "this is not valid json"
                                )
                        )
                )
        );

        when(huggingFaceService.completion(any(HuggingFaceRequest.class)))
                .thenReturn(response);

        // When + Then
        AiAnalysisException exception = assertThrows(
                AiAnalysisException.class,
                () -> huggingFaceLeadAnalyzer.analyze(message)
        );

        assertEquals("Failed to analyze message with AI", exception.getMessage());

        verify(huggingFaceService).completion(any(HuggingFaceRequest.class));
    }
}
