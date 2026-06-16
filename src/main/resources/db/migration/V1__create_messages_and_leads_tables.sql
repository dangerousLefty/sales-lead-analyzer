CREATE TABLE inbound_messages (
    id BIGINT PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE qualified_leads (
    id BIGINT PRIMARY KEY,
    message_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    urgency VARCHAR(50) NOT NULL,
    summary TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_qualified_leads_message
        FOREIGN KEY (message_id)
        REFERENCES inbound_messages(id)
);