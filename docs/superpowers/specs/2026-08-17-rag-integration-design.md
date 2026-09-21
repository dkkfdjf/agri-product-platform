# RAG（检索增强生成）集成设计文档

**版本**: v1.0
**日期**: 2026-08-17
**状态**: 设计中

---

## 一、概述

### 1.1 目标
在农产品融销一体平台的AI助手中集成RAG（检索增强生成）能力，使AI能够基于平台的专业知识库和历史问答数据提供更准确的回答。

### 1.2 核心需求
- **数据源**: 知识库(tb_knowledge) + 问答(tb_question、tb_question_reply)
- **权限控制**: 普通用户检索已发布内容，专家/管理员可检索全部
- **搜索策略**: 基于向量的语义搜索（余弦相似度）
- **返回数量**: 总共3条最相关结果
- **向量存储**: MySQL内轻量级向量数据库

---

## 二、系统架构

### 2.1 整体架构图

```
┌─────────────┐
│  前端AI组件  │
│  (带RAG开关) │
└──────┬──────┘
       │ HTTP POST /api/ai/rag
       ▼
┌─────────────┐
│ AiController│
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  AiService  │◄───────┐
└──────┬──────┘        │
       │               │
       ├───────────────┤
       │               │
       ▼               ▼
┌─────────────┐   ┌─────────────────┐
│VectorSearch │   │TencentMaaService │
│  Service    │   │  (LLM API调用)   │
└──────┬──────┘   └────────┬────────┘
       │                  │
       ▼                  ▼
┌─────────────┐      ┌──────────┐
│MySQL向量DB  │      │ 腾讯云API │
│(向量相似度) │      └──────────┘
└─────────────┘
```

### 2.2 向量数据库设计

**在MySQL中创建轻量级向量数据库**：
- 新增 `tb_vector_index` 表存储文档向量
- 向量存储为JSON数组（1536维，对应text-embedding-ada-002）
- 使用余弦相似度计算
- 批量向量化现有知识库和问答数据

### 2.2 数据流向

1. 用户发送消息（可选开启RAG）
2. 前端调用 `/api/ai/rag` 接口
3. 后端解析JWT获取用户角色
4. 根据用户角色和RAG开关决定搜索策略
5. 并行搜索知识库和问答数据
6. 构建增强Prompt（上下文 + 历史消息 + 当前问题）
7. 调用腾讯云LLM API
8. 返回AI回复 + 引用来源

---

## 三、接口设计

### 3.1 新增接口

#### POST /api/ai/rag
RAG增强的AI对话接口

**请求体:**
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

**响应体:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "reply": "根据知识库信息，小麦常见病虫害包括...",
    "sources": [
      {"type": "knowledge", "title": "小麦病虫害防治指南", "id": 123, "similarity": 0.89},
      {"type": "question", "title": "小麦发黄是什么原因", "id": 456, "similarity": 0.76},
      {"type": "knowledge", "title": "小麦种植技术要点", "id": 124, "similarity": 0.71}
    ],
    "usedRag": true
  }
}
```

### 3.2 向后兼容

保留原有的直接调用模式，当 `useRag=false` 时不进行数据库检索。

---

## 四、核心组件设计

### 4.1 Controller层

**AiController.java**
```java
@RestController
@RequestMapping("/ai")
public class AiController {
    
    @PostMapping("/rag")
    public Result<AiResponse> chatWithRag(@RequestBody AiRequest request) {
        // 1. 获取当前用户角色
        // 2. 调用AiService处理请求
        // 3. 返回AI回复
    }
}
```

### 4.2 Service层

**AiService.java** - 主服务，协调各组件
```java
public String chatWithRag(String message, List<ChatMessage> history,
                          boolean useRag, String userRole) {
    if (useRag) {
        // 1. 向量搜索相关知识
        List<RagDocument> context = vectorSearchService.search(message, userRole);
        // 2. 构建增强Prompt
        String enhancedPrompt = buildRagPrompt(message, history, context);
        // 3. 调用LLM
        return tencentMaaService.chat(enhancedPrompt, history);
    } else {
        return tencentMaaService.chat(message, history);
    }
}
```

**VectorSearchService.java** - 向量搜索服务
```java
public List<RagDocument> search(String query, String userRole) {
    // 1. 将查询文本向量化
    // 2. 从tb_vector_index加载所有向量
    // 3. 计算余弦相似度排序
    // 4. 根据用户权限过滤
    // 5. 返回top 3最相关结果
}
```

**TencentMaaService.java** - 腾讯云API调用
```java
public String chat(String message, List<ChatMessage> history) {
    // 1. 构建API请求
    // 2. 调用腾讯云MaaS API
    // 3. 解析响应返回内容
}
```

### 4.3 数据库访问

**RagDocument.java** - 统一的检索结果格式
```java
@Data
public class RagDocument {
    private String type;        // "knowledge" | "question"
    private Integer id;         // 文档ID
    private String title;       // 标题
    private String content;     // 内容摘要
    private Double similarity; // 相似度分数 (0-1)
}
```

**VectorIndex.java** - 向量索引实体
```java
@Data
@TableName("tb_vector_index")
public class VectorIndex {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String docType;      // "knowledge" | "question"
    private Integer docId;        // 对应文档ID
    private String vector;       // JSON数组格式的向量 [0.1, -0.2, ...]
    private String contentHash;  // 内容哈希，用于变化检测
    private LocalDateTime updateTime;
}
```

---

## 五、向量搜索策略

### 5.1 向量化流程

**文本向量化：**
- 使用腾讯云MaaS Embedding API（或其他embedding服务）
- 将文本转换为1536维向量（兼容OpenAI embedding格式）
- 存储为JSON数组到MySQL

**批量向量化现有数据：**
```java
// 启动时自动向量化或通过管理接口触发
public void buildVectorIndex() {
    // 1. 获取所有知识库和问答
    // 2. 对每个文档调用embedding API
    // 3. 存储到tb_vector_index表
}
```

### 5.2 向量相似度计算

**余弦相似度公式：**
```
similarity = (A · B) / (||A|| × ||B||)
```

**Java实现：**
```java
public double cosineSimilarity(double[] vectorA, double[] vectorB) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;

    for (int i = 0; i < vectorA.length; i++) {
        dotProduct += vectorA[i] * vectorB[i];
        normA += Math.pow(vectorA[i], 2);
        normB += Math.pow(vectorB[i], 2);
    }

    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}
```

### 5.3 搜索流程

```
1. 用户提问 → 向量化查询文本
2. 从tb_vector_index加载所有向量
3. 逐个计算余弦相似度
4. 按相似度降序排序
5. 根据用户权限过滤结果
6. 返回top 3结果
```

### 5.4 权限过滤

**在相似度排序后过滤：**
```java
// 普通用户只保留已发布内容
if (!canViewUnpublished(userRole)) {
    results = results.filter(doc ->
        (doc.type.equals("knowledge") && doc.status == 1) ||
        (doc.type.equals("question") && doc.status == 1)
    );
}
```

---

## 六、权限控制

### 6.1 角色权限矩阵

| 角色 | 知识库访问 | 问答访问 |
|:-----|:----------:|:--------:|
| farmer/buyer | 已发布(status=1) | 已回答(status=1) |
| expert/admin | 全部 | 全部 |

### 6.2 权限判断逻辑

```java
private boolean canViewUnpublished(String userRole) {
    return "expert".equals(userRole) || "admin".equals(userRole);
}
```

---

## 七、Prompt模板设计

### 7.1 RAG增强系统提示

```
你是农产品融销一体平台的智能助手。请根据以下参考信息回答用户问题：

【参考信息】
知识库：
{知识库内容}

问答记录：
{问答内容}

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
【知识库1】小麦病虫害防治指南
小麦常见病害包括锈病、白粉病等...

【问答1】小麦发黄是什么原因？
专家回答：小麦发黄可能是缺氮、病虫害或水分问题...
```

---

## 八、配置管理

### 8.1 后端配置

**application.yml 新增:**
```yaml
tencent:
  maas:
    api-key: ${TENCENT_API_KEY}
    api-url: https://tokenhub-intl.tencentmaas.com/v1/chat/completions
    model: hy3-preview
    embedding-url: ${TENCENT_EMBEDDING_URL:https://api.openai.com/v1/embeddings}
    embedding-model: ${TENCENT_EMBEDDING_MODEL:text-embedding-ada-002}
    timeout: 30000

rag:
  enabled: true
  max-results: 3           # 总共返回3条最相关结果
  min-similarity: 0.6      # 最低相似度阈值
```

**application-local.yml:**
```yaml
tencent:
  maas:
    api-key: sk-FZnYXzi4YsSYqz9WOlVNRBvKtJtEcuEOha3U3nBwPMGVwTjc
```

### 8.2 环境变量

```bash
TENCENT_API_KEY=sk-xxx
TENCENT_EMBEDDING_URL=https://api.openai.com/v1/embeddings
TENCENT_EMBEDDING_MODEL=text-embedding-ada-002
RAG_ENABLED=true
```

---

## 九、错误处理

### 9.1 降级策略

| 失败场景 | 处理方式 |
|:---------|:---------|
| 数据库搜索异常 | 降级到普通对话模式，记录日志 |
| 无搜索结果 | 仍然调用LLM，但不加上下文 |
| LLM调用失败 | 返回友好错误提示，显示重试按钮 |
| 超时 | 30秒超时，返回超时提示 |

### 9.2 错误响应格式

```json
{
  "code": 500,
  "message": "AI服务暂时不可用，请稍后重试",
  "data": {
    "errorType": "LLM_TIMEOUT",
    "fallbackReply": null
  }
}
```

---

## 十、数据库设计

### 10.1 向量索引表

**新增表结构：tb_vector_index**

```sql
CREATE TABLE tb_vector_index (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doc_type VARCHAR(20) NOT NULL COMMENT '文档类型: knowledge/question',
    doc_id INT NOT NULL COMMENT '对应文档ID',
    vector TEXT NOT NULL COMMENT '向量JSON数组',
    content_hash VARCHAR(64) COMMENT '内容哈希，用于变化检测',
    update_time DATETIME NOT NULL,
    INDEX idx_doc (doc_type, doc_id),
    INDEX idx_hash (content_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量索引表';
```

### 10.2 现有表优化

**可选全文索引（提升关键词匹配）：**
```sql
-- 知识库表
ALTER TABLE tb_knowledge ADD FULLTEXT INDEX ft_title_content (title, content);

-- 问答表
ALTER TABLE tb_question ADD FULLTEXT INDEX ft_title_question (title, question);
```

### 10.3 向量化任务表

**新增表结构：tb_vector_task（管理向量化任务）**

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

## 十一、前端改动

### 11.1 需要修改的文件

| 文件 | 改动内容 |
|:-----|:---------|
| `src/api/ai.js` | 新增 `sendMessageWithRag()` 方法，移除API密钥配置 |
| `src/stores/ai.js` | 新增 `useRag` 状态，默认为 `true` |
| `src/components/AIAssistant/*` | 可选：添加RAG开关UI |

### 11.2 新增API方法

```javascript
// src/api/ai.js
export async function sendMessageWithRag(messages, useRag = true) {
  const response = await request.post('/api/ai/rag', {
    message: messages[messages.length - 1].content,
    useRag: useRag,
    messages: messages
  })
  return response.data
}
```

### 11.3 移除的配置

`.env` 文件中移除：
```
VITE_TENCENT_API_KEY
VITE_TENCENT_API_URL
VITE_TENCENT_MODEL
```

---

## 十二、测试计划

### 12.1 单元测试

- [ ] VectorSearchService向量搜索逻辑测试
- [ ] 余弦相似度计算测试
- [ ] 权限过滤测试
- [ ] Prompt构建测试
- [ ] 向量化API调用测试

### 12.2 集成测试

- [ ] RAG接口端到端测试
- [ ] 不同角色权限测试
- [ ] 降级场景测试
- [ ] 向量索引增量更新测试

### 12.3 手动测试场景

| 场景 | 预期结果 |
|:-----|:---------|
| 用户提问农业问题 | 基于知识库回答，显示相似度 |
| 用户提问已有问答 | 引用历史回答 |
| 专家提问 | 可以引用草稿内容 |
| 无相关内容 | 仍然有AI回复，但不加上下文 |
| API调用失败 | 降级到普通模式 |
| 向量相似度过低 | 过滤掉低相似度结果 |

---

## 十三、实施步骤

1. **数据库准备** (Phase 1)
   - 创建tb_vector_index向量索引表
   - 创建tb_vector_task任务管理表
   - 数据初始化脚本

2. **向量化服务** (Phase 2)
   - 实现EmbeddingService调用embedding API
   - 批量向量化现有知识库和问答数据
   - 实现向量索引增量更新机制

3. **后端RAG开发** (Phase 3)
   - 创建AiController、AiService
   - 实现VectorSearchService向量搜索
   - 实现TencentMaaService LLM调用
   - 权限控制和相似度过滤

4. **前端适配** (Phase 4)
   - 修改API调用方式
   - 更新状态管理
   - 可选UI优化（显示相似度）

5. **测试验证** (Phase 5)
   - 单元测试
   - 集成测试
   - 用户验收测试

---

## 十四、后续优化方向

1. **向量数据库升级**: 迁移到专用向量数据库（如Milvus、Pinecone）
2. **混合检索**: 结合向量搜索和关键词搜索
3. **查询优化**: 向量近似搜索（ANN）提升性能
4. **缓存机制**: 常见问题结果缓存减少API调用
5. **反馈学习**: 收集用户反馈优化检索策略
6. **多数据源扩展**: 加入产品信息、融资信息作为检索源

---

## 附录

### A. 相关文件清单

**后端新增:**
- `src/main/java/com/agri/platform/controller/AiController.java`
- `src/main/java/com/agri/platform/service/AiService.java`
- `src/main/java/com/agri/platform/service/VectorSearchService.java`
- `src/main/java/com/agri/platform/service/EmbeddingService.java`
- `src/main/java/com/agri/platform/service/TencentMaaService.java`
- `src/main/java/com/agri/platform/service/VectorIndexService.java`
- `src/main/java/com/agri/platform/entity/VectorIndex.java`
- `src/main/java/com/agri/platform/entity/VectorTask.java`
- `src/main/java/com/agri/platform/dto/AiRequest.java`
- `src/main/java/com/agri/platform/dto/AiResponse.java`
- `src/main/java/com/agri/platform/dto/ChatMessage.java`
- `src/main/java/com/agri/platform/dto/RagDocument.java`
- `src/main/java/com/agri/platform/mapper/VectorIndexMapper.java`
- `src/main/java/com/agri/platform/mapper/VectorTaskMapper.java`

**前端修改:**
- `src/api/ai.js`
- `src/stores/ai.js`

### B. 数据库脚本

**新增SQL文件:**
- `src/main/resources/sql/vector_index.sql` - 向量索引表创建
- `src/main/resources/sql/vector_task.sql` - 向量化任务表创建
- `src/main/resources/sql/vector_data_init.sql` - 初始数据向量化

### C. 配置检查清单

- [ ] 腾讯云API密钥已配置
- [ ] Embedding API已配置（OpenAI或腾讯云）
- [ ] 向量索引表已创建
- [ ] 现有数据已向量化
- [ ] 前端环境变量已清理
- [ ] 后端配置文件已更新

---

**文档状态**: 已更新（向量搜索版本）
**预计开发时间**: 3-4天
**风险等级**: 中等（涉及embedding API调用和向量计算）
**风险等级**: 低（轻量级实现，向后兼容）
