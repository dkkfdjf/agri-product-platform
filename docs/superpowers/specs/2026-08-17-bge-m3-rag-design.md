# BGE-M3 RAG 系统设计文档

**版本**: v1.0
**日期**: 2026-08-17
**状态**: 设计中

---

## 一、概述

### 1.1 目标
使用腾讯云 BGE-M3 模型为农产品平台的 AI 助手实现 RAG（检索增强生成）功能，提升回答的准确性和专业性。

### 1.2 核心需求
- **Embedding 模型**: 腾讯云 BGE-M3 (多语言 embedding)
- **LLM 模型**: 腾讯云 hy3-preview
- **数据源**: 知识库(tb_knowledge) + 问答(tb_question、tb_question_reply)
- **切片策略**: 固定 500 字符长度切片
- **向量存储**: MySQL 存储 JSON 格式向量
- **返回数量**: Top-3 最相关结果
- **权限控制**: 普通用户检索已发布内容，专家/管理员可检索全部

---

## 二、系统架构

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                        用户交互层                             │
│  前端 AI 组件 → POST /api/ai/rag → RAG API               │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────────┐
│                      后端服务层                              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │ AiRag        │    │ VectorSearch │    │ BgeM3        │  │
│  │ Controller    │←───│ Service      │←───│ Embedding    │  │
│  └──────┬───────┘    └──────┬───────┘    └──────┬───────┘  │
│         │                    │                    │          │
│         └────────────────────┴────────────────────┘          │
│                              ↓                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              DocumentChunkerService                    │  │
│  │        (固定500字符切片 + 内容变更检测)               │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────┬────────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────────┐
│                      数据层                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │ tb_vector    │    │ tb_knowledge │    │ tb_question  │  │
│  │ _index       │    │              │    │              │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
└─────────────────────────────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────────┐
│                   外部服务                                   │
│  ┌──────────────────────────────────────────────────────┐  │
│  │          腾讯云 MaaS API                             │  │
│  │  • BGE-M3 Embedding API                             │  │
│  │  • hy3-preview Chat API                             │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 数据流程

**文档处理流程：**
```
知识库/问答内容更新
    ↓
DocumentChunkerService 切片 (500字符)
    ↓
内容哈希检测 (跳过未变更切片)
    ↓
BgeM3EmbeddingService 向量化
    ↓
存储到 tb_vector_index
```

**RAG 查询流程：**
```
用户提问
    ↓
AiRagController 接收请求
    ↓
BgeM3EmbeddingService 问题向量化
    ↓
VectorSearchService 余弦相似度搜索
    ↓
权限过滤 (已发布 vs 全部)
    ↓
构建 RAG Prompt (Top-3 结果)
    ↓
TencentMaaService LLM 生成回答
    ↓
返回 AI 回复 + 引用来源
```

---

## 三、接口设计

### 3.1 RAG 聊天接口

#### POST /api/ai/rag
RAG 增强的 AI 对话接口

**请求体：**
```json
{
  "message": "小麦病虫害如何防治？",
  "useRag": true,
  "messages": [
    {"role": "user", "content": "你好"},
    {"role": "assistant", "content": "你好，我是农产品平台助手"}
  ]
}
```

**响应体：**
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
        "title": "小麦病虫害防治指南",
        "content": "小麦常见病害包括锈病、白粉病等...",
        "similarity": 0.89
      },
      {
        "type": "question",
        "id": 456,
        "title": "小麦发黄是什么原因",
        "content": "专家回答：小麦发黄可能是缺氮...",
        "similarity": 0.76
      },
      {
        "type": "knowledge",
        "id": 124,
        "title": "小麦种植技术要点",
        "content": "小麦种植需要注意...",
        "similarity": 0.71
      }
    ],
    "usedRag": true
  }
}
```

### 3.2 向量管理接口

#### POST /api/ai/vector/rebuild
重建向量索引（管理员）

#### GET /api/ai/vector/status
获取向量化状态

---

## 四、核心组件设计

### 4.1 文档切片服务

**DocumentChunkerService.java**
```java
@Service
public class DocumentChunkerService {
    private static final int CHUNK_SIZE = 500; // 固定500字符

    /**
     * 将文档切分为固定长度的块
     */
    public List<DocumentChunk> chunkDocument(String content, String docType, Integer docId);

    /**
     * 检测内容是否变更
     */
    private boolean hasContentChanged(String content, String existingHash);

    /**
     * 生成内容哈希
     */
    private String generateContentHash(String content);
}
```

**DocumentChunk 数据结构：**
```java
@Data
public class DocumentChunk {
    private String docType;        // "knowledge" | "question"
    private Integer docId;         // 原文档 ID
    private Integer chunkIndex;    // 切片索引
    private String content;        // 切片内容
    private String contentHash;    // 内容哈希
}
```

### 4.2 BGE-M3 Embedding 服务

**BgeM3EmbeddingService.java**
```java
@Service
public class BgeM3EmbeddingService {
    private final TencentMaaProperties properties;

    /**
     * 批量文本向量化（最多支持32个文本）
     */
    public List<double[]> embedBatch(List<String> texts);

    /**
     * 单个文本向量化
     */
    public double[] embed(String text);

    /**
     * 调用腾讯云 BGE-M3 API
     */
    private List<double[]>> callBgeM3Api(List<String> texts);
}
```

**腾讯云 BGE-M3 API 调用示例：**
```bash
curl -X POST 'https://tokenhub.tencentmaas.com/v1/embeddings' \
  -H 'Authorization: Bearer sk-JGPwOyGMWiFsbnURRa74GW00ik8hCBhoQwVs4zjmz9tEP6mj' \
  -H 'Content-Type: application/json' \
  -d '{
    "model": "bge-m3",
    "input": ["小麦病虫害防治", "农业种植技术"]
  }'
```

**API 响应格式：**
```json
{
  "data": [
    {
      "embedding": [0.1, -0.2, 0.3, ...],
      "index": 0
    }
  ],
  "model": "bge-m3",
  "usage": {
    "prompt_tokens": 10,
    "total_tokens": 10
  }
}
```

### 4.3 向量搜索服务

**VectorSearchService.java**
```java
@Service
public class VectorSearchService {
    private final VectorIndexMapper vectorIndexMapper;

    /**
     * 向量搜索
     */
    public List<RagDocument> search(String query, String userRole, int topK);

    /**
     * 余弦相似度计算
     */
    private double cosineSimilarity(double[] vectorA, double[] vectorB);

    /**
     * 权限过滤
     */
    private List<RagDocument> filterByPermission(List<RagDocument> results, String userRole);
}
```

**RagDocument 数据结构：**
```java
@Data
public class RagDocument {
    private String type;         // "knowledge" | "question"
    private Integer id;         // 文档 ID
    private String title;       // 标题
    private String content;     // 内容摘要
    private Double similarity;  // 相似度分数 (0-1)
}
```

### 4.4 AI RAG 控制器

**AiRagController.java**
```java
@RestController
@RequestMapping("/ai")
public class AiRagController {
    private final AiRagService aiRagService;

    @PostMapping("/rag")
    public Result<AiRagResponse> chatWithRag(@RequestBody AiRagRequest request);
}
```

---

## 五、数据库设计

### 5.1 向量索引表

**tb_vector_index (向量索引表)**
```sql
CREATE TABLE tb_vector_index (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doc_type VARCHAR(20) NOT NULL COMMENT '文档类型: knowledge/question',
    doc_id INT NOT NULL COMMENT '对应文档ID',
    chunk_index INT NOT NULL COMMENT '切片索引',
    chunk_content TEXT NOT NULL COMMENT '切片内容',
    vector JSON NOT NULL COMMENT 'BGE-M3向量数组',
    content_hash VARCHAR(64) COMMENT '内容哈希，用于变化检测',
    update_time DATETIME NOT NULL,
    INDEX idx_doc (doc_type, doc_id),
    INDEX idx_hash (content_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量索引表';
```

### 5.2 向量化任务表

**tb_vector_task (向量化任务管理表)**
```sql
CREATE TABLE tb_vector_task (
    id INT PRIMARY KEY AUTO_INCREMENT,
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型: rebuild/update/delete',
    doc_type VARCHAR(20) NOT NULL,
    doc_id INT NOT NULL,
    status TINYINT DEFAULT 0 COMMENT '0待处理 1处理中 2完成 3失败',
    error_msg VARCHAR(255),
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量化任务表';
```

---

## 六、配置管理

### 6.1 application.yml 配置

```yaml
# 腾讯云 MaaS API 配置
tencent:
  maas:
    api-key: ${TENCENT_API_KEY}
    api-url: ${TENCENT_API_URL:https://tokenhub.tencentmaas.com/v1/chat/completions}
    model: ${TENCENT_MODEL:hy3-preview}
    embedding-url: ${TENCENT_EMBEDDING_URL:https://tokenhub.tencentmaas.com/v1/embeddings}
    embedding-model: ${TENCENT_EMBEDDING_MODEL:bge-m3}
    timeout: 30000

# RAG 配置
rag:
  enabled: true
  chunk-size: 500              # 切片大小（字符）
  max-results: 3               # 返回结果数量
  min-similarity: 0.6          # 最低相似度阈值
  batch-size: 10               # 批量向量化大小
```

---

## 七、Prompt 模板设计

### 7.1 RAG 系统提示

```
你是农产品融销一体平台的智能助手。请根据以下参考信息回答用户问题：

【参考信息】
{知识库内容}

【注意事项】
1. 优先使用参考信息中的内容回答
2. 如果参考信息不足，可以基于农业知识补充
3. 保持简洁、友好的语气
4. 回答时提及"根据平台知识库..."或"根据历史问答..."增强可信度
5. 必要时引导用户使用平台功能

用户问题：{用户消息}
```

### 7.2 上下文构建格式

```
【参考1】小麦病虫害防治指南 (相似度: 0.89)
小麦常见病害包括锈病、白粉病等...

【参考2】小麦发黄是什么原因？ (相似度: 0.76)
专家回答：小麦发黄可能是缺氮、病虫害或水分问题...

【参考3】小麦种植技术要点 (相似度: 0.71)
小麦种植需要注意...
```

---

## 八、权限控制

### 8.1 角色权限矩阵

| 角色 | 知识库访问 | 问答访问 |
|:-----|:----------:|:--------:|
| farmer/buyer | 已发布(status=1) | 已回答(status=1) |
| expert/admin | 全部 | 全部 |

### 8.2 权限判断逻辑

```java
private boolean canViewUnpublished(String userRole) {
    return "expert".equals(userRole) || "admin".equals(userRole);
}
```

---

## 九、错误处理

### 9.1 降级策略

| 失败场景 | 处理方式 |
|:---------|:---------|
| 向量化失败 | 记录任务失败，后台重试 |
| 向量搜索异常 | 降级到普通对话模式 |
| 无搜索结果 | 仍然调用 LLM，但不加上下文 |
| LLM 调用失败 | 返回友好错误提示 |
| 超时 | 30秒超时，返回超时提示 |

---

## 十、实施步骤

### Phase 1: 基础设施
1. 创建数据库表 (tb_vector_index, tb_vector_task)
2. 添加配置项
3. 创建基础实体类和 DTO

### Phase 2: Embedding 服务
1. 实现 BgeM3EmbeddingService
2. 实现文档切片 DocumentChunkerService
3. 测试向量化功能

### Phase 3: 向量搜索
1. 实现 VectorSearchService
2. 实现余弦相似度计算
3. 测试向量搜索功能

### Phase 4: RAG 集成
1. 实现 AiRagController 和 AiRagService
2. 集成 LLM 调用
3. 权限控制

### Phase 5: 测试验证
1. 单元测试
2. 集成测试
3. 用户验收测试

---

## 十一、后续优化方向

1. **向量数据库升级**: 迁移到专用向量数据库
2. **混合检索**: 结合向量搜索和关键词搜索
3. **查询优化**: 向量近似搜索 (ANN) 提升性能
4. **缓存机制**: 常见问题结果缓存
5. **反馈学习**: 收集用户反馈优化检索策略

---

**文档状态**: 待审核
**预计开发时间**: 4-5天
**风险等级**: 中等
