package com.hamza.smartleadgenerator.exceptions;

public class MessageNotFoundException extends RuntimeException{
    public MessageNotFoundException(Long messageId) {
        super("Message with id " + messageId + " was not found");
    }
}
