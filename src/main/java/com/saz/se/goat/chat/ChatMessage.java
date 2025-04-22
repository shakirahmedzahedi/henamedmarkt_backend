package com.saz.se.goat.chat;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;        // "Agent" or "Client"
    private String message;
    private String clientId;      // 8-digit UUID string
    private LocalDateTime timestamp;

    // Constructors
    public ChatMessage() {}

    public ChatMessage(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }

}