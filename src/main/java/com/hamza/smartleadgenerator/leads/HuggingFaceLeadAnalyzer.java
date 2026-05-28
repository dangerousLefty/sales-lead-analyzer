package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.ai.ChatCompletionResponse;
import com.hamza.smartleadgenerator.ai.HuggingFaceRequest;
import com.hamza.smartleadgenerator.ai.HuggingFaceService;
import com.hamza.smartleadgenerator.message.InboundMessage;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@Primary
public class HuggingFaceLeadAnalyzer implements LeadAnalyzer{

    private final HuggingFaceService huggingFaceService;
    private final ObjectMapper objectMapper;

    private final String systemPrompt =
            """
You are a lead qualification assistant.

Analyze the inbound message and decide whether it is a qualified lead.

Return ONLY valid JSON. Do not include markdown. Do not include explanations.

Use this exact JSON shape:
{
  "qualified": true,
  "title": "short lead title or null",
  "type": "demo-request | pricing-inquiry | partnership | support | other | null",
  "urgency": "low | medium | high | null",
  "summary": "short summary or null"
}

A qualified lead is someone showing business interest, purchase intent, demo interest, pricing interest, partnership interest, or urgent support/billing need.
General compliments, simple office-hour questions, password reset questions, student comments, or shipping questions are not qualified leads.
""";

    public HuggingFaceLeadAnalyzer(HuggingFaceService huggingFaceService, ObjectMapper objectMapper) {
        this.huggingFaceService = huggingFaceService;
        this.objectMapper = objectMapper;
    }

    @Override
    public LeadAnalysisResult analyze(InboundMessage message) {
        HuggingFaceRequest request = new HuggingFaceRequest(
                List.of(
                        new HuggingFaceRequest.Message("system", systemPrompt),
                        new HuggingFaceRequest.Message("user", message.content())
                ),
                "meta-llama/Llama-3.3-70B-Instruct:groq",
                false
        );

        ChatCompletionResponse response = huggingFaceService.completion(request);

        try {
            AiLeadAnalysisResponse aiResponse = objectMapper.readValue(
                    response.content(),
                    AiLeadAnalysisResponse.class
            );

            if (!aiResponse.qualified()){
                return new LeadAnalysisResult(false, null, null, null, null);
            }

            return new LeadAnalysisResult(
                    true,
                    aiResponse.title(),
                    parseLeadType(aiResponse.type()),
                    parseUrgency(aiResponse.urgency()),
                    aiResponse.summary()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Hugging Face response", e);
        }

    }

    private LeadType parseLeadType(String type) {
        return LeadType.valueOf(type.toUpperCase().replace("-", "_"));
    }

    private UrgencyLevel parseUrgency(String urgency) {
        return UrgencyLevel.valueOf(urgency.toUpperCase());
    }
}
