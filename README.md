# Sales Lead Analyzer

Smart Lead Generator is a Spring Boot backend application that: 
- Accepts inbound website contact messages.
- Stores them in PostgreSQL
- Uses Hugging Face AI to determine whether each message should become a qualified sales lead.

If a message qualifies as a lead, the application stores structured lead data including a title, lead type, urgency level, and summary.

The application supports two lead qualification dispatch modes:

- `local`: uses an in-process async dispatcher.
- `sqs`: sends qualification jobs to AWS SQS and processes them through an SQS listener.

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
- Unit tests for service and dispatcher/listener behavior

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Flyway
- Hugging Face Inference Providers
- AWS SQS
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
- Qualification layer: dispatches lead qualification jobs using either local async processing or AWS SQS

Basic flow:

```text
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
LeadQualificationDispatcher
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

```bash
HF_TOKEN=your_hugging_face_token_here
```

## Run Modes

The application supports two lead qualification dispatch modes.

### Local Mode

Local mode uses an in-process async dispatcher.

```properties
spring.profiles.active=local
```

MessageService
    → AsyncLeadQualificationDispatcher
    → LeadQualificationService
    → Hugging Face

### AWS SQS Mode

SQS mode sends a lightweight qualification job to AWS SQS. The job contains only the saved message ID.

```properties
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
```JSON
{
  "messageId": 1
}
```

The full message is loaded from PostgreSQL using the message ID.

## Environment Variables

The application requires the following environment variables:

| Variable | Description |
|---|---|
| `HF_TOKEN` | Hugging Face access token used to call the AI model |
| `AWS_ACCESS_KEY_ID` | AWS access key for SQS access |
| `AWS_SECRET_ACCESS_KEY` | AWS secret key for SQS access |
| `AWS_REGION` | AWS region where the SQS queue exists, e.g. `us-east-1` |

For IntelliJ:

1. Go to `Run → Edit Configurations`
2. Select the Spring Boot run configuration
3. Add the environment variables
4. Restart the application

## Application Configuration

Example `application.properties`:

```properties
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

# Profile
spring.profiles.active=local
```

use spring.profiles.active=local for local async processing.
or
use spring.profiles.active=sqs for AWS SQS processing.

## Sample Requests

### Submit an inbound message

```http
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
```http
GET http://localhost:8080/api/v1/messages
```

### View qualified leads
```http
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
```http
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
```properties
spring.jpa.hibernate.ddl-auto=validate
```
This means Flyway owns schema creation and Hibernate only validates that the Java entities match the database tables.

## AWS SQS Setup
In SQS mode, the application sends a lightweight job to AWS SQS after an inbound message is saved.

```Queue name:
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

## Future improvements

- Add pagination and sorting for message and lead endpoints
- Add filtering by lead type and urgency
- Extend docker-compose.yml to run the full app stack (currently Postgres only)
- Add LocalStack support for local SQS development
- Add more integration tests
- Add CI with GitHub Actions
- Deploy to Railway, Render, or AWS
- Add actuator health checks
- Add rate limiting for AI qualification calls
