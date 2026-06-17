package com.hamza.smartleadgenerator.message;

import com.hamza.smartleadgenerator.leads.*;
import com.hamza.smartleadgenerator.qualification.LeadQualificationDispatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final LeadQualificationDispatcher leadQualificationDispatcher;

    public MessageService(MessageRepository messageRepository,
                          LeadQualificationDispatcher leadQualificationDispatcher
    ) {
        this.messageRepository = messageRepository;
        this.leadQualificationDispatcher = leadQualificationDispatcher;
    }

    public MessageResponse createMessage(MessageRequest messageRequest) {

        InboundMessage savedMessage =  messageRepository.save(
                new InboundMessage(
                null,
                messageRequest.content(),
                LocalDateTime.now()
        ));

        leadQualificationDispatcher.dispatch(savedMessage);

        return new MessageResponse(
                savedMessage.id(),
                savedMessage.content(),
                savedMessage.createdAt()
        );
    }

    public List<MessageResponse> getMessages() {
        return messageRepository.findAll()
                .stream()
                .map(message -> new MessageResponse(
                        message.id(),
                        message.content(),
                        message.createdAt()
                )).toList();
    }

}
