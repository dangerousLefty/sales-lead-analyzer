package com.hamza.smartleadgenerator.leads;


import com.hamza.smartleadgenerator.exceptions.LeadNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeadController.class)
public class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeadService leadService;

    @Test
    void returnLeadsSuccessful() throws Exception {
        //Given
        LeadResponse response = new LeadResponse(
                1L,
                10L,
                "Pricing inquiry",
                LeadType.PRICING_INQUIRY,
                UrgencyLevel.HIGH,
                "Customer wants pricing details.",
                LocalDateTime.now()
        );

        when(leadService.getLeads()).thenReturn(List.of(response));
        // When
        mockMvc.perform(get("/api/v1/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].messageId").value(10))
                .andExpect(jsonPath("$[0].title").value("Pricing inquiry"))
                .andExpect(jsonPath("$[0].type").value("PRICING_INQUIRY"))
                .andExpect(jsonPath("$[0].urgencyLevel").value("HIGH"))
                .andExpect(jsonPath("$[0].summary").value("Customer wants pricing details."));
        //Then
        verify(leadService).getLeads();

    }

    @Test
    void returnLeadSuccess() throws Exception {
        //Given
        LeadResponse response = new LeadResponse(
                1L,
                10L,
                "Pricing inquiry",
                LeadType.PRICING_INQUIRY,
                UrgencyLevel.HIGH,
                "Customer wants pricing details.",
                LocalDateTime.now()
        );

        when(leadService.getLead(response.id())).thenReturn(response);
        // When
        mockMvc.perform(get("/api/v1/leads/" + response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.messageId").value(10))
                .andExpect(jsonPath("$.title").value("Pricing inquiry"))
                .andExpect(jsonPath("$.type").value("PRICING_INQUIRY"))
                .andExpect(jsonPath("$.urgencyLevel").value("HIGH"))
                .andExpect(jsonPath("$.summary").value("Customer wants pricing details."));

        //Then
        verify(leadService).getLead(response.id());
    }

    @Test
    void returnLeadFailure() throws Exception {
        // Given
        when(leadService.getLead(999L))
                .thenThrow(new LeadNotFoundException(999L));

        // When + Then
        mockMvc.perform(get("/api/v1/leads/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lead with id 999 was not found"));

        verify(leadService).getLead(999L);
    }
}
