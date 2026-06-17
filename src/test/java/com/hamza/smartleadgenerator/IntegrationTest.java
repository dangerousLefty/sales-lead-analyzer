package com.hamza.smartleadgenerator;

import com.hamza.smartleadgenerator.leads.LeadAnalysisResult;
import com.hamza.smartleadgenerator.leads.LeadAnalyzer;
import com.hamza.smartleadgenerator.leads.LeadType;
import com.hamza.smartleadgenerator.leads.UrgencyLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "ai.huggingface.token=test-token"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeadAnalyzer leadAnalyzer;
    @Autowired
    private ObjectMapper objectMapper;

    /*
    @Test
    void givenValidMessage_whenPostMessage_thenMessageIsCreated() throws Exception {
        // Given
        when(leadAnalyzer.analyze(any()))
                .thenReturn(new LeadAnalysisResult(
                        false,
                        null,
                        null,
                        null,
                        null
                ));

        // When + Then
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Your website looks great!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Your website looks great!"));
    } */

    /*
    @Test
    void givenQualifiedLeadExists_whenGetLeadById_thenReturnLead() throws Exception {
        // Given

        when(leadAnalyzer.analyze(any()))
                .thenReturn(new LeadAnalysisResult(
                        true,
                        "Pricing inquiry",
                        LeadType.PRICING_INQUIRY,
                        UrgencyLevel.HIGH,
                        "Customer wants pricing details."
                ));

        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "content": "Can you send me pricing details?"
                            }
                            """))
                .andExpect(status().isOk());

        waitUntilLeadExists();

        String leadsJson = mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long leadId = objectMapper.readTree(leadsJson)
                .get(0)
                .get("id")
                .asLong();

        // When + Then
        mockMvc.perform(get("/api/v1/leads/{leadId}", leadId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(leadId))
                .andExpect(jsonPath("$.title").value("Pricing inquiry"))
                .andExpect(jsonPath("$.type").value("PRICING_INQUIRY"))
                .andExpect(jsonPath("$.urgencyLevel").value("HIGH"))
                .andExpect(jsonPath("$.summary").value("Customer wants pricing details."));
    } */

    private void waitUntilLeadExists() throws Exception {
        long deadline = System.currentTimeMillis() + 3000;
        AssertionError lastError = null;

        while (System.currentTimeMillis() < deadline) {
            try {
                mockMvc.perform(get("/api/v1/leads"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[0].messageId").value(1))
                        .andExpect(jsonPath("$[0].title").value("Pricing inquiry"))
                        .andExpect(jsonPath("$[0].type").value("PRICING_INQUIRY"))
                        .andExpect(jsonPath("$[0].urgencyLevel").value("HIGH"))
                        .andExpect(jsonPath("$[0].summary").value("Customer wants pricing details."));

                return;
            } catch (AssertionError error) {
                lastError = error;
                Thread.sleep(100);
            }
        }

        throw lastError;
    }
}