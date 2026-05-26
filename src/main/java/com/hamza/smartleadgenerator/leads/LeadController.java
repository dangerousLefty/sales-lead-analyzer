package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.message.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/leads")
public class LeadController {
    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping("/{leadId}")
    public ResponseEntity<LeadResponse> getLead(@PathVariable Long leadId){
        Optional<LeadResponse> leadResponse = leadService.getLead(leadId);

        if (leadResponse.isPresent()){
            return ResponseEntity.ok(leadResponse.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<LeadResponse>> getLeads(){
        return ResponseEntity.ok(leadService.getLeads());
    }
}
