package com.hamza.smartleadgenerator.messages;

import com.hamza.smartleadgenerator.leads.LeadQualificationService;
import com.hamza.smartleadgenerator.message.*;
import com.hamza.smartleadgenerator.qualification.AsyncLeadQualificationDispatcher;
import com.hamza.smartleadgenerator.qualification.LeadQualificationDispatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    //private LeadQualificationService leadQualificationService;
    private LeadQualificationDispatcher leadQualificationDispatcher;

    @InjectMocks
    private MessageService messageService;

    @Test
    void createMessageSuccess() {
        //Given
        MessageRequest request = new MessageRequest(
                "Can you send me pricing details?"
        );

        when(messageRepository.save(any(InboundMessage.class)))
                .thenAnswer(invocation -> {
                    InboundMessage message = invocation.getArgument(0);

                    return new InboundMessage(
                            1L,
                            message.content(),
                            message.createdAt()
                    );
                });

        // When
        MessageResponse response = messageService.createMessage(request);

        // Then
        assertEquals(1L, response.id());
        assertEquals("Can you send me pricing details?", response.content());
        assertNotNull(response.createdAt());

        verify(messageRepository).save(any(InboundMessage.class));
        //verify(leadQualificationService).qualifyMessage(any(InboundMessage.class));
        verify(leadQualificationDispatcher).dispatch(any(InboundMessage.class));

    }

    @Test
    void getMessagesSuccess() {
        // Given
        InboundMessage message = new InboundMessage(
                1L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        when(messageRepository.findAll())
                .thenReturn(List.of(message));
        // When
        List<MessageResponse> responses = messageService.getMessages();

        // Then
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).id());
        assertEquals("Can you send me pricing details?", responses.get(0).content());
        assertEquals(message.createdAt(), responses.get(0).createdAt());

        verify(messageRepository).findAll();
    }
}
