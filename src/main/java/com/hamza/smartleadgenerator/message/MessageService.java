package com.hamza.smartleadgenerator.message;

import com.hamza.smartleadgenerator.leads.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final LeadQualificationService leadQualificationService;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public MessageService(MessageRepository messageRepository,
                          LeadQualificationService leadQualificationService
    ) {
        this.messageRepository = messageRepository;
        this.leadQualificationService = leadQualificationService;
    }

    public MessageResponse createMessage(MessageRequest messageRequest) {

        InboundMessage savedMessage =  messageRepository.save(
                new InboundMessage(
                idGenerator.getAndIncrement(),
                messageRequest.content(),
                LocalDateTime.now()
        ));

        leadQualificationService.qualifyMessage(savedMessage);

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
