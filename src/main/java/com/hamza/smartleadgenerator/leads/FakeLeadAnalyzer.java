package com.hamza.smartleadgenerator.leads;

import com.hamza.smartleadgenerator.message.InboundMessage;
import org.springframework.stereotype.Service;

@Service
public class FakeLeadAnalyzer {

    public LeadAnalysisResult analyze(InboundMessage message){
        String content = message.content();

        if (content.contains("pricing") ||
                content.contains("price") ||
                content.contains("plan")
        ){
            return new LeadAnalysisResult(
                    true,
                    "Pricing Inquiry",
                    LeadType.PRICING_INQUIRY,
                    UrgencyLevel.HIGH,
                    "Customer is asking about pricing plan or details"
            );
        }

        if (content.contains("demo") || content.contains("walk us through")) {
            return new LeadAnalysisResult(
                    true,
                    "Demo request",
                    LeadType.DEMO_REQUEST,
                    UrgencyLevel.MEDIUM,
                    "Customer is asking for a demo or product walkthrough."
            );
        }

        if (content.contains("partnership") || content.contains("integration")) {
            return new LeadAnalysisResult(
                    true,
                    "Partnership inquiry",
                    LeadType.PARTNERSHIP,
                    UrgencyLevel.MEDIUM,
                    "Customer is interested in partnership or integration."
            );
        }

        if (content.contains("billing") || content.contains("charged") || content.contains("asap")) {
            return new LeadAnalysisResult(
                    true,
                    "Support request",
                    LeadType.SUPPORT,
                    UrgencyLevel.HIGH,
                    "Customer needs help with a support issue."
            );
        }

        return new LeadAnalysisResult(
                false,
                null,
                null,
                null,
                null
        );
    }
}
