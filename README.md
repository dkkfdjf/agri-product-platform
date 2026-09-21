# 农产品融销一体平台（融销通）

融资 + 交易一体化平台：银行信贷产品对接农户融资，货源 / 需求双向撮合交易，集成**支付宝当面付**与 **Qwen3 AI Agent（Function Calling）**。

> 在线演示：[nongronghui.echeng.xyz](https://nongronghui.echeng.xyz)
> 技术栈：Spring Boot 3.2 · MyBatis-Plus · MySQL · Redis · JWT · 支付宝 SDK · Vue 3 · Qwen3 Agent

## 核心特性

### 1. 融资业务闭环
```
银行发布产品 → 农户申请 → 审批事务内自动生成等额本息还款计划
            (BigDecimal 精算、末期校正) → 还款两阶段(凭证+银行确认) → 信誉分闭环
```

### 2. 交易与支付
- 货源 / 需求双向发布 → 下单 → **支付宝当面付**（二维码 + 回调幂等 + 数据库状态校验）
- 银行**四维评分**（信用 / 联合贷 / 活跃度 / 负担）+ 7:3 融合排序，银行间数据相互隔离
- 五角色权限体系 + 注册审核

### 3. AI Agent（Function Calling）
- 经 OpenAI 兼容 API 接入 **Qwen3-30B-A3B(MoE)**：自拼 tools、解析 tool_calls
- 按角色过滤下发 5 个工具（查信誉 / 查融资 / 查行情 / RAG 问答），模型自主多轮调用真实 Service
- **role_scope 行级隔离**约束返回数据，结果 PII 脱敏，调用全量落 `tool_log` 审计

### 4. RAG 知识库问答
- 文档切块 → bge-m3 向量嵌入 → 检索增强生成，农业政策 / 金融知识即时问答

## 架构

```
┌──────────────┐    ┌───────────────────────────────────┐
│  Vue 3 前端   │───▶│  Spring Boot 3.2                  │
│ (五角色权限)  │    │  ├─ JWT + Redis 会话               │
└──────────────┘    │  ├─ 融资: 审批/还款计划/信誉分       │
                    │  ├─ 交易: 撮合/支付宝当面付(幂等回调) │
┌──────────────┐    │  ├─ Agent: Qwen3 + Function Calling│
│  支付宝平台    │◀───│  │   (tools 按角色下发, PII 脱敏)   │
│ (扫码/回调)   │    │  └─ RAG: 切块→bge-m3→检索→生成      │
└──────────────┘    └───────┬──────────────┬────────────┘
                      ┌─────▼────┐   ┌─────▼─────┐
                      │  MySQL   │   │   Redis   │
                      └──────────┘   └───────────┘
```

## 快速开始

```bash
# 1. 后端（密钥走 application-local.yml，已 gitignore；支付宝/腾讯MaaS 按需配置）
cd backend/agri-product-platform-backend
# 创建 application-local.yml：MySQL 密码、JWT secret、支付宝密钥等
./mvnw spring-boot:run

# 2. 前端
cd frontend/agri-product-platform-frontend
cp .env.example .env        # 填入 API 地址等
npm install
npm run dev
```

## 目录结构

```
├── backend/agri-product-platform-backend/   # Spring Boot 3.2 后端
│   └── src/main/java/com/agri/platform/
│       ├── controller/          # 融资/交易/支付/AI 接口层
│       ├── service/             # Agent、RAG、还款计划精算等核心服务
│       └── entity/ mapper/      # MyBatis-Plus 数据层
├── frontend/agri-product-platform-frontend/  # Vue 3 + Vite + Element Plus
│   └── src/components/AIAssistant/          # AI 对话组件
├── tests/                       # AI 评估器与测试数据集
└── docs/                        # 设计文档
```

## 联系方式

- GitHub: [dkkfdjf](https://github.com/dkkfdjf)
- 邮箱: 19574486919@163.com
