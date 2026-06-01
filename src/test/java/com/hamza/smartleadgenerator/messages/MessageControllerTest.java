package com.hamza.smartleadgenerator.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hamza.smartleadgenerator.exceptions.LeadNotFoundException;
import com.hamza.smartleadgenerator.message.MessageController;
import com.hamza.smartleadgenerator.message.MessageRequest;
import com.hamza.smartleadgenerator.message.MessageResponse;
import com.hamza.smartleadgenerator.message.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

//    @Autowired
//    private ObjectMapper objectMapper;

    @Test
    void getMessagesSuccess() throws Exception{
        //Given
        MessageResponse response = new MessageResponse(
                1L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );
        when(messageService.getMessages()).thenReturn(List.of(response));
        // When
        mockMvc.perform(get("/api/v1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Can you send me pricing details?"));
        //Then
        verify(messageService).getMessages();
    }

    @Test
    void postMessageSuccess() throws Exception {
        // Given
        MessageRequest request = new MessageRequest(
                "Can you send me pricing details?"
        );

        MessageResponse response = new MessageResponse(
                1L,
                "Can you send me pricing details?",
                LocalDateTime.now()
        );

        when(messageService.createMessage(any(MessageRequest.class)))
                .thenReturn(response);

        // When + Then
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "content": "Can you send me pricing details?"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Can you send me pricing details?"));

        verify(messageService).createMessage(any(MessageRequest.class));

    }

    @Test
    void postMessageFail() throws Exception {
        // When + Then
        mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "content": ""
                            }
                            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("content is required"));
    }
}
