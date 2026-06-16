package com.hamza.smartleadgenerator.message.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "inbound_messages")
public class InboundMessageEntity {

    public InboundMessageEntity(Long id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    protected InboundMessageEntity() {
    }

    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}