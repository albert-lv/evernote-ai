# Evernote AI

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**[中文文档](./README_CN.md)** | English

An intelligent AI assistant for Evernote that provides weekly summaries and retrieval-augmented generation (RAG) capabilities. This project demonstrates how to build a production-ready AI system that integrates with Evernote, performs semantic search, and generates contextual responses using vector databases and LLMs.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Reusable Components](#reusable-components)
- [Development](#development)
- [License](#license)

## Features

### 1. Weekly Summary Generation
- Automatically generates AI-powered summaries of your weekly Evernote activities
- Streams responses in real-time using Server-Sent Events (SSE)
- Analyzes note content and provides insightful summaries

### 2. Retrieval-Augmented Generation (RAG)
- Semantic search across your Evernote content using vector embeddings
- Context-aware responses based on your actual notes
- Real-time streaming responses for better user experience

### 3. Automated Synchronization
- Background jobs to sync Evernote notes to vector database
- Support for local files synchronization
- Incremental sync with date tracking

## Architecture

The project follows a clean, layered architecture:

```
┌─────────────────────────────────────────────────┐
│           REST API Layer                         │
│  (EverNoteController - SSE Streaming)           │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│        Service Layer (Core Business Logic)       │
│  • EverNoteAiService: AI orchestration          │
│  • LlmService: LLM interaction                  │
│  • VectorStoreService: Vector operations        │
│  • EmbeddingService: Text embeddings            │
│  • DocumentService: Text processing             │
│  • EverNoteClientService: Evernote API          │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         Background Jobs & Utilities              │
│  • EverNoteSyncJob: Sync notes to vector DB    │
│  • LocalFilesSyncJob: Sync local files         │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│         External Integrations                    │
│  • Evernote API                                 │
│  • OpenAI / Ollama / Vertex AI                  │
│  • Milvus Vector Database                       │
└─────────────────────────────────────────────────┘
```

## Technologies

### Core Framework
- **Spring Boot 3.2.2** - Application framework
- **Java 21** - Programming language
- **Maven** - Build tool

### AI & Machine Learning
- **Spring AI 0.8.0** - AI framework for Java
  - OpenAI integration
  - Ollama integration (local LLM support)
  - Vertex AI integration
  - Transformer embeddings
- **JTokkit 1.0.0** - Token counting for OpenAI models

### Vector Database
- **Milvus 2.3.4** - High-performance vector database for semantic search

### Document Processing
- **Apache Tika 2.9.0** - Text extraction and document parsing
- **Juniversalchardet 1.0.3** - Character encoding detection

### Evernote Integration
- **Evernote API 1.25.1** - Official Evernote SDK

### Utilities
- **Guava 33.0.0** - Google Core Libraries
- **Resilience4j** - Circuit breaker and retry logic
- **Lombok** - Reduce boilerplate code
- **Spotless** - Code formatting

## Getting Started

### Prerequisites

1. **Java 21** or higher
2. **Maven 3.6+**
3. **Milvus** vector database (can be run via Docker)
4. **Evernote Developer Token** - [Get one here](https://dev.evernote.com/doc/)
5. **OpenAI API Key** (or other LLM provider credentials)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/albert-lv/evernote-ai.git
cd evernote-ai
```

2. Create `application.properties` file:
```bash
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

3. Configure the application (see [Configuration](#configuration))

4. Build the project:
```bash
./mvnw clean package
```

5. Run the application:
```bash
./mvnw spring-boot:run
```

## Configuration

Create or update `src/main/resources/application.properties`:

```properties
# OpenAI Configuration
spring.ai.openai.chat.base-url=https://api.openai.com
spring.ai.openai.chat.api-key=your-api-key
spring.ai.openai.chat.options.model=gpt-3.5-turbo-16k
spring.ai.openai.chat.options.temperature=0.7

# OpenAI Embedding Configuration
spring.ai.openai.embedding.base-url=https://api.openai.com
spring.ai.openai.embedding.api-key=your-api-key

# OpenAI Image Configuration (optional)
spring.ai.openai.image.base-url=https://api.openai.com
spring.ai.openai.image.api-key=your-api-key

# Ollama Configuration (for local LLM)
spring.ai.ollama.chat.options.model=llama2-chinese
spring.ai.ollama.chat.options.temperature=0.7

# Vertex AI Configuration (optional)
spring.ai.vertex.ai.base-url=your-vertex-ai-url
spring.ai.vertex.ai.api-key=your-api-key
spring.ai.vertex.ai.chat.model=gemini-pro
spring.ai.vertex.ai.chat.options.temperature=0.7

# Resilience4j Retry Configuration
resilience4j.retry.configs.default.maxRetryAttempts=3
resilience4j.retry.configs.default.waitDuration=1000

# Evernote Developer Token
evernote.developer.token=your-evernote-dev-token
```

## API Endpoints

### 1. Weekly Summary
```
GET /api/v1/evernote/ai/weekly/summary
```
Generates an AI-powered weekly summary of your Evernote activities. Returns a streaming response via SSE.

**Response:** Server-Sent Events (SSE) stream

**Example:**
```bash
curl -N http://localhost:8080/api/v1/evernote/ai/weekly/summary
```

### 2. RAG Query
```
GET /api/v1/evernote/ai/rag?query=<your-question>
```
Performs semantic search across your notes and generates context-aware responses.

**Parameters:**
- `query` (required): Your question or search query

**Response:** Server-Sent Events (SSE) stream

**Example:**
```bash
curl -N "http://localhost:8080/api/v1/evernote/ai/rag?query=What%20did%20I%20learn%20about%20Spring%20Boot%3F"
```

## Reusable Components

This project contains several reusable components that can be adapted for other applications:

### 1. Document Processing Service
**Location:** `org.albert.evernote.ai.service.document.DocumentService`

**Purpose:** Parse and split documents into processable chunks

**Key Features:**
- HTML/XML to plain text conversion using Apache Tika
- Intelligent text chunking for embedding models
- Character encoding detection

**Reusability:** Can be used in any document processing pipeline that needs to prepare text for LLM or embedding models.

### 2. Embedding Service
**Location:** `org.albert.evernote.ai.service.embedding.EmbeddingService`

**Purpose:** Generate vector embeddings from text

**Key Features:**
- Support for multiple embedding models (OpenAI, Transformers)
- Batch processing capabilities
- Model-agnostic interface

**Reusability:** Drop-in solution for any application needing text embeddings.

### 3. Vector Store Service
**Location:** `org.albert.evernote.ai.service.vector.VectorStoreService`

**Purpose:** Manage vector database operations

**Key Features:**
- Insert and search vector documents
- Milvus integration with connection pooling
- Generic vector document model

**Reusability:** Can be adapted for any vector database use case (semantic search, recommendation systems, etc.).

### 4. LLM Service
**Location:** `org.albert.evernote.ai.service.llm.LlmService`

**Purpose:** Interact with Large Language Models

**Key Features:**
- Support for multiple LLM providers (OpenAI, Ollama, Vertex AI)
- Streaming response support
- Retry logic with Resilience4j

**Reusability:** Generic interface for LLM interaction that can be used in any AI application.

### 5. Sync Job Framework
**Location:** `org.albert.evernote.ai.job.*`

**Purpose:** Background synchronization of content to vector database

**Key Features:**
- Incremental sync with checkpoint persistence
- Error handling and rate limiting
- Modular design for different data sources (Evernote, local files)

**Reusability:** Template for building background sync jobs for any data source.

### 6. Milvus Client Wrapper
**Location:** `org.albert.evernote.ai.service.vector.milvus.MilvusClient`

**Purpose:** Simplified Milvus vector database operations

**Key Features:**
- High-level API for common operations
- Connection management
- Schema and collection management

**Reusability:** Can be extracted as a standalone Milvus utility library.

## Development

### Code Formatting
This project uses Spotless for code formatting:

```bash
# Check formatting
./mvnw spotless:check

# Apply formatting
./mvnw spotless:apply
```

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package -DskipTests
java -jar target/evernote-ai-0.0.1-SNAPSHOT.jar
```

## Project Structure

```
src/main/java/org/albert/evernote/ai/
├── controller/          # REST API endpoints
├── service/
│   ├── document/       # Document processing
│   ├── embedding/      # Text embeddings
│   ├── evernote/       # Evernote integration
│   ├── llm/            # LLM interaction
│   └── vector/         # Vector database operations
├── job/                # Background sync jobs
├── util/               # Utility classes
└── constant/           # Constants

src/test/java/           # Unit and integration tests
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring AI](https://spring.io/projects/spring-ai) - AI framework for Java
- [Milvus](https://milvus.io/) - Vector database
- [Evernote API](https://dev.evernote.com/) - Evernote SDK
- [Apache Tika](https://tika.apache.org/) - Content analysis toolkit