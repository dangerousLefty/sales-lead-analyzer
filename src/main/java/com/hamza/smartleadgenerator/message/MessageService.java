package com.hamza.smartleadgenerator.message;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public MessageResponse createMessage(MessageRequest messageRequest) {
        InboundMessage savedMessage = new InboundMessage(
                idGenerator.getAndIncrement(),
                messageRequest.content(),
                LocalDateTime.now()
        );

        messageRepository.save(savedMessage);

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
