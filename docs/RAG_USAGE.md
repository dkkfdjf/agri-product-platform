# BGE-M3 RAG System Usage Guide

## API Endpoints

### POST /api/ai/rag
RAG-enhanced AI chat endpoint

**Request:**
```json
{
  "message": "小麦病虫害如何防治？",
  "useRag": true,
  "messages": [{"role": "user", "content": "Previous message"}]
}
```

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "reply": "根据知识库信息，小麦常见病虫害包括...",
    "sources": [
      {
        "type": "knowledge",
        "id": 123,
        "title": "Document 123",
        "content": "相关内容...",
        "similarity": 0.89
      }
    ],
    "usedRag": true
  }
}
```

### GET /api/ai/vector/status
Check RAG system status

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": "RAG system operational"
}
```

### POST /api/ai/vector/rebuild
Rebuild vector index (admin only)

**Response:**
```json
{
  "code": 200,
  "message": "success",
  "data": "Vector rebuild initiated"
}
```

## Configuration

Add to application-local.yml:
```yaml
tencent:
  maas:
    api-key: your_api_key_here
    embedding-url: https://tokenhub.tencentmaas.com/v1/embeddings
    embedding-model: bge-m3
```

## Testing

1. Start application: `mvn spring-boot:run`

2. Test RAG endpoint:
```bash
curl -X POST 'http://localhost:8080/api/ai/rag' \
  -H 'Content-Type: application/json' \
  -d '{
    "message": "小麦病虫害如何防治？",
    "useRag": true,
    "messages": []
  }'
```

3. Test non-RAG endpoint:
```bash
curl -X POST 'http://localhost:8080/api/ai/rag' \
  -H 'Content-Type: application/json' \
  -d '{
    "message": "你好",
    "useRag": false,
    "messages": []
  }'
```

## Architecture

```
User Question → AiRagController
              ↓
         AiRagService
              ↓
    ┌─────────┴─────────┐
    ↓                   ↓
VectorSearchService  BgeM3EmbeddingService
    ↓                   ↓
  MySQL           Tencent Cloud API
(Cosine Similarity) (BGE-M3 Model)
    ↓
    AiRagService
    ↓
  Tencent Cloud API (hy3-preview)
    ↓
  AI Response + Sources
```

## Components

- **DocumentChunkerService**: Splits documents into 500-character chunks
- **BgeM3EmbeddingService**: Converts text to vectors using BGE-M3
- **VectorSearchService**: Performs cosine similarity search
- **AiRagService**: Orchestrates RAG workflow
- **AiRagController**: Provides REST endpoints

## Data Flow

1. Document created/updated → DocumentChunkerService splits into chunks
2. Chunks → BgeM3EmbeddingService → vectors → MySQL storage
3. User question → BgeM3EmbeddingService → query vector
4. VectorSearchService → cosine similarity → top-3 results
5. AiRagService builds context → calls LLM → returns response with sources
