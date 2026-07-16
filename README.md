# Sales Lead Analyzer

Smart Lead Generator is a Spring Boot backend application that:

- Accepts inbound website contact messages.
- Stores them in PostgreSQL.
- Uses Hugging Face AI to determine whether each message should become a qualified sales lead.

If a message qualifies as a lead, the application stores structured lead data including a title, lead type, urgency level, and summary.
A React + TypeScript frontend for this backend is available at [sales-lead-analyzer-ui](https://github.com/dangerousLefty/sales-lead-analyzer-ui).

The application supports three lead qualification dispatch modes behind a single `LeadQualificationDispatcher` interface, selected at startup via Spring profile:

- `local`: in-process async dispatcher.
- `sqs`: sends qualification jobs to AWS SQS and processes them through an SQS listener.
- `kafka`: publishes qualification jobs to an Apache Kafka topic and processes them through a `@KafkaListener`, with automatic retry and a dead-letter topic for failed records.

## Features

- Submit and view inbound messages
- Automatically qualify messages using Hugging Face
- Store qualified leads with title, type, urgency, and summary
- PostgreSQL persistence using Spring Data JPA
- Flyway database migrations
- Global exception handling
- Bean Validation for request validation
- Swagger/OpenAPI documentation
- Async local processing mode
- AWS SQS-based processing mode
- Apache Kafka processing mode with dead-letter topic and retry backoff
- Partition-keyed for per-message ordering (Kafka mode)
- Unit tests for service and dispatcher/listener behavior
- CORS configuration for local development against the React frontend on http://localhost:5173

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Hugging Face Inference Providers
- AWS SQS
- Apache Kafka (via Spring for Apache Kafka)
- Docker
- Swagger / OpenAPI
- JUnit
- Mockito

## Architecture

The application follows an N-tier architecture:

- Controller layer: handles HTTP requests and responses
- Service layer: contains business logic
- Repository layer: handles persistence
- AI layer: calls Hugging Face for lead analysis
- Qualification layer: dispatches lead qualification jobs using local async processing, AWS SQS, or Apache Kafka
- The React frontend calls `POST /api/v1/messages` to submit inbount messages and `GET /api/v1/leads` to display qualified leads.

Basic flow:

```
POST /api/v1/messages
        ↓
MessageController
        ↓
MessageService
        ↓
MessageRepository
        ↓
PostgreSQL inbound_messages table
        ↓
LeadQualificationDispatcher   ← local | sqs | kafka
        ↓
LeadQualificationService
        ↓
LeadAnalyzer
        ↓
Hugging Face
        ↓
LeadRepository
        ↓
PostgreSQL qualified_leads table
```

## Configuration

This project requires a Hugging Face access token.
Set the following environment variable:

```
HF_TOKEN=your_hugging_face_token_here
```

## Run Modes

The application supports three lead qualification dispatch modes.

### Local Mode

Local mode uses an in-process async dispatcher.

```
spring.profiles.active=local
```

MessageService
→ AsyncLeadQualificationDispatcher
→ LeadQualificationService
→ Hugging Face

### AWS SQS Mode

SQS mode sends a lightweight qualification job to AWS SQS. The job contains only the saved message ID.

```
spring.profiles.active=sqs
```

MessageService
→ SqsLeadQualificationDispatcher
→ AWS SQS
→ SqsLeadQualificationListener
→ MessageRepository.findById(messageId)
→ LeadQualificationService
→ Hugging Face

The SQS payload contains only:

```
{
  "messageId": 1
}
```

The full message is loaded from PostgreSQL using the message ID.

### Apache Kafka Mode

Kafka mode publishes a lightweight qualification job to an Apache Kafka topic. The job contains only the saved message ID and is keyed on that ID so that all records for a given message land on the same partition (deterministic per-key ordering).

```
spring.profiles.active=kafka
```

MessageService
→ KafkaLeadQualificationDispatcher
→ Apache Kafka topic (`smartlead-lead-qualification`)
→ KafkaLeadQualificationListener (consumer group `smart-lead-generator`)
→ MessageRepository.findById(messageId)
→ LeadQualificationService
→ Hugging Face

The Kafka payload contains only:

```
{
  "messageId": 1
}
```

**Retry and dead-letter handling.** Consumer failures are retried using an exponential backoff (1s → 2s → 4s, max 3 retries), configured via `ExponentialBackOffWithMaxRetries` on Spring Kafka's `DefaultErrorHandler`. After retries are exhausted, the record is published to a dead-letter topic (`smartlead-lead-qualification.DLT`) by a `DeadLetterPublishingRecoverer`, the offset is committed on the main topic, and the partition unblocks so subsequent messages continue to flow. This prevents a single poison-pill record from stalling the entire consumer.

**Local Kafka via Docker.** The `docker-compose.yml` in this repository includes a single-node Kafka broker (KRaft mode, no ZooKeeper) alongside PostgreSQL. Create the topics on first run:

```
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --topic smartlead-lead-qualification --partitions 3 --replication-factor 1

docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --topic smartlead-lead-qualification.DLT --partitions 3 --replication-factor 1
```

## Environment Variables

The application requires the following environment variables:

| Variable                | Description                                             | Required for                 |
| ----------------------- | ------------------------------------------------------- | ---------------------------- |
| `HF_TOKEN`              | Hugging Face access token used to call the AI model     | All modes                    |
| `AWS_ACCESS_KEY_ID`     | AWS access key for SQS access                           | `sqs` mode                   |
| `AWS_SECRET_ACCESS_KEY` | AWS secret key for SQS access                           | `sqs` mode                   |
| `AWS_REGION`            | AWS region where the SQS queue exists, e.g. `us-east-1` | `sqs` mode                   |

For IntelliJ:

1. Go to `Run → Edit Configurations`
2. Select the Spring Boot run configuration
3. Add the environment variables
4. Restart the application

## Running the Frontend

The [sales-lead-analyzer-ui](https://github.com/dangerousLefty/sales-lead-analyzer-ui) repository contains a React + TypeScript frontend that submits inbound messages and displays qualified leads through this backend's REST API.

Clone it as a sibling directory:

​```
~/IdeaProjects/
├── smart-lead-generator/       ← this repo
└── sales-lead-analyzer-ui/     ← frontend repo
​```

Then, with the backend already running:

​```
cd sales-lead-analyzer-ui
npm install
npm run dev
​```

The frontend runs on `http://localhost:5173`. This backend's `CorsConfig` already grants that origin permission to call `/api/**`, so no additional configuration is required for local development.



## Application Configuration

Example `application.properties`:

```
spring.application.name=smart-lead-generator

# Hugging Face
ai.huggingface.token=${HF_TOKEN}

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/smartlead
spring.datasource.username=<your_username>
spring.datasource.password=<your_password>

# JPA / Flyway
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.flyway.enabled=true

# AWS SQS
app.sqs.lead-qualification-queue=smartlead-lead-qualification
spring.cloud.aws.region.static=<your_region_here>

# Apache Kafka
app.kafka.lead-qualification-topic=smartlead-lead-qualification
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.consumer.group-id=smart-lead-generator
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=com.hamza.smartleadgenerator.qualification
spring.kafka.consumer.properties.spring.json.value.default.type=com.hamza.smartleadgenerator.qualification.LeadQualificationJob

# Profile
spring.profiles.active=local
```

Use `spring.profiles.active=local` for local async processing, `spring.profiles.active=sqs` for AWS SQS processing, or `spring.profiles.active=kafka` for Apache Kafka processing.

## Sample Requests

### Submit an inbound message

```
POST http://localhost:8080/api/v1/messages
Content-Type: application/json

{
  "content": "Can you send me pricing details for the Pro plan? We need to decide by Friday."
}

Example response:
{
  "id": 1,
  "content": "Can you send me pricing details for the Pro plan? We need to decide by Friday.",
  "createdAt": "2026-06-16T15:30:00.123"
}
```

### View inbound messages

```
GET http://localhost:8080/api/v1/messages
```

### View qualified leads

```
GET http://localhost:8080/api/v1/leads

Example response:
[
  {
    "id": 1,
    "messageId": 1,
    "title": "Pro Plan Pricing Inquiry",
    "type": "PRICING_INQUIRY",
    "urgency": "HIGH",
    "summary": "Customer needs pricing details for Pro plan with decision deadline by Friday.",
    "createdAt": "2026-06-16T15:30:04.456"
  }
]
```

### View a lead by ID

```
GET http://localhost:8080/api/v1/leads/1
```

## Database Migrations

This project uses Flyway for database migrations.
Migration files are stored in:

```
src/main/resources/db/migration
```

Current migration:

```
V1__create_messages_and_leads_tables.sql
```

Flyway creates:

```
inbound_messages
qualified_leads
flyway_schema_history
```

Hibernate is configured with:

```
spring.jpa.hibernate.ddl-auto=validate
```

This means Flyway owns schema creation and Hibernate only validates that the Java entities match the database tables.

## AWS SQS Setup

In SQS mode, the application sends a lightweight job to AWS SQS after an inbound message is saved.

```
smartlead-lead-qualification
```

Region:

```
us-east-1
```

SQS payload:

```
{
  "messageId": 1
}
```

The listener receives the job, loads the full message from PostgreSQL using messageId, and runs lead qualification.

Required AWS environment variables:

```
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION
```

Use an IAM user with SQS permissions. Do not use root AWS credentials.

## Apache Kafka Setup

In Kafka mode, the application publishes a lightweight job to an Apache Kafka topic after an inbound message is saved. The record is keyed on `messageId` so all records for a given message land on the same partition, giving deterministic per-message ordering.

Main topic:

```
smartlead-lead-qualification
```

Dead-letter topic:

```
smartlead-lead-qualification.DLT
```

Kafka payload (same as SQS):

```
{
  "messageId": 1
}
```

The `KafkaLeadQualificationListener` receives the job, loads the full message from PostgreSQL using `messageId`, and runs lead qualification. Failed records are retried in-process with exponential backoff (1s, 2s, 4s), then routed to the dead-letter topic if all retries fail.

The included `docker-compose.yml` runs a single-node Kafka broker in KRaft mode (no ZooKeeper) for local development. See the "Apache Kafka Mode" section above for the topic creation commands.

## Future improvements

- Add pagination and sorting for message and lead endpoints (done)
- Serve the built React frontend from the Spring Boot backend as static resources in production
- Add authentication and a per-user view of qualified leads
- Add filtering by lead type and urgency
- Add LocalStack support for local SQS development
- Add Kafka Streams for real-time lead metrics
- Add more integration tests
- Add CI with GitHub Actions
- Deploy to Railway, Render, or AWS
- Add actuator health checks
- Add rate limiting for AI qualification calls
