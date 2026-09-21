# AI助手功能设计文档

**日期:** 2026-08-12
**项目:** 农产品融销一体平台 AI助手
**状态:** 待实现

## 1. 功能概述

为农产品平台添加一个智能AI助手，采用浮动聊天窗口形式，所有已登录用户都可以使用。AI助手能够解答农业业务问题和平台操作指导问题。

### 核心目标

- 为所有角色（农户、买家、专家、银行、管理员）提供智能问答服务
- 解答农业业务相关问题（种植、销售、融资等）
- 提供平台操作指导和帮助
- 使用 Sophnet DeepSeek API 作为AI后端

## 2. 架构设计

### 2.1 组件结构

```
src/
├── components/
│   └── AIAssistant/
│       ├── AIAssistant.vue       # 主容器组件
│       ├── AIChatButton.vue      # 浮动按钮（右下角）
│       ├── AIChatWindow.vue      # 聊天主窗口
│       ├── AIMessageList.vue     # 消息列表组件
│       ├── AIMessage.vue         # 单条消息组件
│       ├── AIInputArea.vue       # 输入区域组件
│       └── AILoading.vue         # 加载状态组件
├── api/
│   └── ai.js                     # AI API 调用模块
├── stores/
│   └── ai.js                     # Pinia store（聊天历史、状态）
└── utils/
    └── markdown.js               # Markdown 处理工具
```

### 2.2 数据流

```
用户输入 → AIInputArea → Pinia Store → API调用 → DeepSeek → 响应 → 更新Store → 界面更新
```

### 2.3 布局集成

AI助手组件将在以下布局中集成：
- `FarmerLayout.vue` - 农户工作台
- `BuyerLayout.vue` - 买家工作台
- `ExpertLayout.vue` - 专家工作台
- `BankLayout.vue` - 银行工作台
- `AdminLayout.vue` - 管理员后台
- `MainLayout.vue` - 主布局（公共页面）

## 3. UI设计

### 3.1 浮动按钮

**位置：** 右下角固定，距离底部24px，距离右侧24px
**样式：**
- 圆形按钮，直径56px
- Element Plus 的 `ChatDotRound` 图标
- 有未读消息时显示红点提示
- 支持动画效果（悬停、点击）

### 3.2 聊天窗口

**尺寸：**
- 宽度：380px
- 高度：600px（最大高度：屏幕高度的80%）
- 圆角：12px

**结构：**
- **头部：** AI助手名称 + 关闭按钮 + 清空历史按钮
- **主体：** 消息列表区域，可滚动
- **底部：** 输入框 + 发送按钮

**消息样式：**
- 用户消息：右对齐，绿色背景
- AI消息：左对齐，灰色背景
- 支持 Markdown 渲染（代码块、列表、链接等）

### 3.3 状态显示

- **加载中：** 打字动画或 "正在思考..." 提示
- **错误：** 红色错误提示，支持重试
- **空状态：** 欢迎语 + 建议问题示例

## 4. API集成

### 4.1 环境变量配置

在 `.env` 文件中添加：

```bash
VITE_DEEPSEEK_API_KEY=your_api_key_here
VITE_DEEPSEEK_API_URL=https://www.sophnet.com/api/open-apis/v1/chat/completions
```

### 4.2 API请求格式

```javascript
{
  model: "DeepSeek-V4-Flash",
  messages: [
    { role: "system", content: "系统提示词" },
    { role: "user", content: "用户问题" },
    // ... 历史对话
  ],
  max_tokens: 2048,
  temperature: 0.7
}
```

### 4.3 API响应处理

- 成功：提取 `choices[0].message.content` 作为回答
- 错误：显示友好的错误提示
- 超时：30秒超时，提示用户重试

## 5. 聊天历史管理

### 5.1 存储策略

- **内存存储：** Pinia store 管理当前会话
- **持久化：** localStorage 存储历史记录（可选）
- **限制：** 保存最近50条消息

### 5.2 会话上下文

- **系统提示词：** 定义AI助手角色和职责
- **历史消息：** 携带最近N轮对话（如最近10条）
- **清空历史：** 用户可手动清空聊天记录

### 5.3 状态管理

使用 Pinia store 管理以下状态：

```javascript
{
  isOpen: false,           // 聊天窗口是否打开
  messages: [],            // 消息列表
  isLoading: false,        // 是否正在加载
  error: null,             // 错误信息
  hasNewMessage: false     // 是否有新消息
}
```

## 6. 系统提示词

AI助手的角色定义：

```
你是农产品融销一体平台的智能助手，可以帮助用户解答以下问题：

1. 农业业务咨询：种植技术、病虫害防治、农产品销售、市场价格等
2. 平台操作指导：如何发布产品、如何下单、如何申请融资等
3. 融资服务：贷款产品、申请流程、还款方式等
4. 专家服务：如何预约专家、查看专家专长等
5. 其他相关问题

请用简洁、友好的语气回答，必要时引导用户使用平台相关功能。
```

## 7. 权限控制

### 7.1 访问控制

- **显示条件：** 只有已登录用户可以看到AI助手按钮
- **检查方式：** 使用 `useUserStore()` 检查 `isLoggedIn()`
- **实现位置：** 在各个布局组件中条件渲染

### 7.2 组件集成

在每个布局组件中添加：

```vue
<template>
  <div class="layout">
    <!-- 现有布局内容 -->
    <AIAssistant v-if="userStore.isLoggedIn()" />
  </div>
</template>
```

## 8. 错误处理

### 8.1 网络错误

- 显示友好错误提示："网络连接失败，请检查网络后重试"
- 提供重试按钮

### 8.2 API错误

- **401/403：** "API密钥无效，请联系管理员"
- **429：** "请求过于频繁，请稍后再试"
- **500：** "服务暂时不可用，请稍后重试"

### 8.3 边界情况

- **输入为空：** 禁用发送按钮
- **超长输入：** 限制2000字，超出时提示
- **连续发送：** 防止重复提交，发送中禁用按钮

## 9. 依赖项

### 9.1 需要安装

```bash
npm install marked dompurify
```

- **marked：** Markdown 转 HTML
- **dompurify：** HTML 净化，防止 XSS 攻击

### 9.2 使用现有依赖

- Vue 3 - 组件和响应式
- Element Plus - UI 组件
- Pinia - 状态管理
- Axios - HTTP 请求

## 10. 测试策略

### 10.1 组件测试

- AIChatButton 点击打开/关闭
- 消息发送和接收
- 历史记录保存/恢复
- 错误状态显示

### 10.2 集成测试

- API 调用是否正常
- 登录/未登录状态控制
- 多角色权限验证

### 10.3 手动测试场景

1. 问农业问题（种植、销售）
2. 问平台操作问题
3. 测试网络错误情况
4. 测试字数限制
5. 测试历史记录清空
6. 测试 Markdown 渲染

## 11. 实现步骤

1. 安装依赖包（marked、dompurify）
2. 配置环境变量
3. 创建 Pinia store（stores/ai.js）
4. 创建 AI API 调用模块（api/ai.js）
5. 创建 AIAssistant 组件结构
6. 实现浮动按钮（AIChatButton.vue）
7. 实现聊天窗口（AIChatWindow.vue）
8. 实现消息列表（AIMessageList.vue、AIMessage.vue）
9. 实现输入区域（AIInputArea.vue）
10. 实现加载状态（AILoading.vue）
11. 在各个布局中集成组件
12. 添加登录状态控制
13. 实现错误处理
14. 测试和调优

## 12. 后续优化

- 支持流式响应（如果 API 支持）
- 添加语音输入功能
- 支持多语言切换
- 添加常见问题快捷入口
- 支持导出聊天记录
- 添加使用统计和分析
