package com.saz.se.goat.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;


    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageRepository chatMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
    }

    // Handle messages from clients (send to agent's chat room)
    @MessageMapping("/sendMessageToAgent")
    @SendTo("/chat/agent")
    public ChatMessage handleClientMessage(ChatMessage message) {
        chatMessageRepository.save(message);
        return message;
    }

    // Handle incoming messages from the agent
    @MessageMapping("/sendMessageToClient")
    public void handleAgentMessage(@Payload ChatMessage message) {
        chatMessageRepository.save(message);
        String clientQueue = "/queue/" + message.getClientId();
        messagingTemplate.convertAndSend(clientQueue, message);
    }

    // Example method for the agent to start a conversation with the client
    @MessageMapping("/startChatWithClient")  // /app/startChatWithClient
    public void startChatWithClient(@Payload String clientId) {
        String clientQueue = "/queue/" + clientId;  // The client-specific queue
        messagingTemplate.convertAndSend(clientQueue, new ChatMessage("Agent", "Hello! How can I help you today?"));
    }

    @CrossOrigin
    @GetMapping("/{clientId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String clientId) {
        List<ChatMessage> messages = chatMessageRepository.findByClientIdOrderByTimestampAsc(clientId);
        System.out.println("New endpoint");
        return ResponseEntity.ok(messages);
    }

    // REST endpoint: Get list of all distinct clients
    @CrossOrigin
    @GetMapping("/clients")
    public ResponseEntity<List<String>> getAllClients() {
        System.out.println("New endpoint");
        return ResponseEntity.ok(chatMessageRepository.findAllDistinctClientIdsOrderByLatestMessage());
    }
}
