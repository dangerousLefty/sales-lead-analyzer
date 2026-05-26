package com.hamza.smartleadgenerator.message;

import com.hamza.smartleadgenerator.leads.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final FakeLeadAnalyzer fakeLeadAnalyzer;
    private final LeadService leadService;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public MessageService(MessageRepository messageRepository,
                          FakeLeadAnalyzer fakeLeadAnalyzer,
                          LeadService leadService
    ) {
        this.messageRepository = messageRepository;
        this.fakeLeadAnalyzer = fakeLeadAnalyzer;
        this.leadService = leadService;
    }

    public MessageResponse createMessage(MessageRequest messageRequest) {

        InboundMessage savedMessage =  messageRepository.save(
                new InboundMessage(
                idGenerator.getAndIncrement(),
                messageRequest.content(),
                LocalDateTime.now()
        ));
        LeadAnalysisResult result = fakeLeadAnalyzer.analyze(savedMessage);

        if (result.qualified()){
            leadService.createLeadFromMessage(savedMessage, result);
        }
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
