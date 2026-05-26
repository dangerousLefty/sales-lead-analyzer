package com.hamza.smartleadgenerator.message;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public MessageResponse createMessage(@Valid @RequestBody MessageRequest request){
        return messageService.createMessage(request);
    }

    @GetMapping
    public List<MessageResponse> getAllMessages(){
        return messageService.getMessages();
    }
}
