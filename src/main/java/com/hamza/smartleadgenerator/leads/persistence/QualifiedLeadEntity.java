package com.hamza.smartleadgenerator.leads.persistence;

import com.hamza.smartleadgenerator.leads.LeadType;
import com.hamza.smartleadgenerator.leads.UrgencyLevel;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qualified_leads")
public class QualifiedLeadEntity {
    @Id
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", nullable = false)
    private UrgencyLevel urgencyLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected QualifiedLeadEntity() {
    }

    public QualifiedLeadEntity(
            Long id,
            Long messageId,
            String title,
            LeadType type,
            UrgencyLevel urgencyLevel,
            String summary,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.messageId = messageId;
        this.title = title;
        this.type = type;
        this.urgencyLevel = urgencyLevel;
        this.summary = summary;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getMessageId() { return messageId; }
    public String getTitle() { return title; }
    public LeadType getType() { return type; }
    public UrgencyLevel getUrgency() { return urgencyLevel; }
    public String getSummary() { return summary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
