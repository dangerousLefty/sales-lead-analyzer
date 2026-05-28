package com.hamza.smartleadgenerator.exceptions;

public class LeadNotFoundException extends RuntimeException{
    public LeadNotFoundException(Long leadId) {
        super("Lead with id " + leadId + " was not found");
    }
}
