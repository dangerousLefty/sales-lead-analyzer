# Smart Lead Generator

Smart Lead Generator is a Spring Boot backend application that:
- Accepts inbound website messages
- Analyzes them using Hugging Face
- Creates qualified leads when the message shows business intent

## Features

- Submit inbount messages
- View submitted messages
- Automatically analyze messages with Hugging Face
- Create qualified leads with title, type, urgency, and summary
- View all qualified leads
- View a qualified lead by ID
- Input validation
- Global exception handling
- Async processing of leads in background
- OpenAPI/Swagger documentation
- Docker support

## Tech Stack

- Java 17
- Spring Boot
- Maven
- Hugging Face Inference Providers
- JUnit 5
- Mockito
- Docker
- OpenAPI/Swagger

## Configuration

This project requires a Hugging Face access token.
Set the following environment variable:

```bash
HF_TOKEN=your_hugging_face_token_here
```

## Setup 

## 1. Running Locally

clone the repository and navigate to the project folder
Set your Hugging Face token:
```bash
export HF_TOKEN=your_hugging_face_token_here
```

Run the application
```bash
./mvnw spring-boot:run
```
The API will be available at:
```bash
http://localhost:8080
```

## 2. Running with Docker

Build the docker image:
```bash
docker build -t smart-lead-generator .
```

Run the container:
```bash
docker run -p 8080:8080 -e HF_TOKEN=your_hugging_face_token_here smart-lead-generator
```


## Swagger / OpenAPI
Swagger UI is available at:
```bash
http://localhost:8080/swagger-ui/index.html
```

## Example request
```bash
curl -X POST http://localhost:8080/api/v1/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Can you send me pricing details for the Pro plan? We need to decide by Friday."
  }'
```

## Example response
```bash
{
  "id": 1,
  "content": "Can you send me pricing details for the Pro plan? We need to decide by Friday.",
  "createdAt": "2026-06-02T12:00:00"
}
```
Note: Because lead qualification runs asynchronously, 
the lead may appear a moment after the message is submitted.

## Lead Types
The AI classifies qualified leads into one of the following types:

* DEMO_REQUEST
* PRICING_INQUIRY
* PARTNERSHIP
* SUPPORT
* OTHER

## Urgency Levels

* LOW
* MEDIUM
* HIGH


## Architecture
This project uses an N-tier backend structure:
```bash
Controller → Service → Repository
```

Main flow: 
```bash
POST /api/v1/messages
    ↓
MessageController
    ↓
MessageService saves the inbound message
    ↓
LeadQualificationService runs asynchronously
    ↓
LeadAnalyzer analyzes the message using Hugging Face
    ↓
LeadService stores a qualified lead if one is detected
```