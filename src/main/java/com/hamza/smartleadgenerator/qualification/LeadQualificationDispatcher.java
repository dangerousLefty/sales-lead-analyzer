package com.hamza.smartleadgenerator.qualification;

import com.hamza.smartleadgenerator.message.InboundMessage;

public interface LeadQualificationDispatcher {
    void dispatch(InboundMessage message);
}
