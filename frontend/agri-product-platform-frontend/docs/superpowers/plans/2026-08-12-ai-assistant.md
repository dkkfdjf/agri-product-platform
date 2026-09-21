# AI助手功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**目标:** 为农产品平台添加一个浮动聊天窗口形式的AI助手，所有已登录用户都可以使用，能够解答农业业务问题和平台操作指导问题。

**架构:** 使用 Vue 3 组件构建浮动聊天界面，通过 Pinia 管理聊天状态，直接调用 Sophnet DeepSeek API 获取 AI 回答。

**技术栈:** Vue 3, Element Plus, Pinia, Axios, marked, DOMPurify

## 全局约束

- Vue 3 Composition API 语法
- Element Plus UI 组件库
- Pinia 状态管理
- 使用项目现有的 `@/` 别名路径
- 遵循项目现有代码风格（单引号、2空格缩进）
- 仅对已登录用户（`isLoggedIn() === true`）显示 AI 助手
- 使用 Sophnet DeepSeek API: `https://www.sophnet.com/api/open-apis/v1/chat/completions`
- 模型: `DeepSeek-V4-Flash`
- 环境变量配置: `VITE_DEEPSEEK_API_KEY`, `VITE_DEEPSEEK_API_URL`

---

## 文件结构

### 新建文件

```
src/
├── components/
│   └── AIAssistant/
│       ├── AIAssistant.vue       # 主容器组件，控制聊天窗口开关
│       ├── AIChatButton.vue      # 浮动按钮组件
│       ├── AIChatWindow.vue      # 聊天窗口容器
│       ├── AIMessageList.vue     # 消息列表组件
│       ├── AIMessage.vue         # 单条消息组件
│       ├── AIInputArea.vue       # 输入区域组件
│       ├── AILoading.vue         # 加载状态组件
│       └── index.js              # 组件导出
├── api/
│   └── ai.js                     # AI API 调用模块
├── stores/
│   └── ai.js                     # Pinia store
└── utils/
    └── markdown.js               # Markdown 处理工具
```

### 修改文件

```
src/layouts/MainLayout.vue           # 添加 AIAssistant 组件
src/layouts/FarmerLayout.vue         # 无需修改（使用 MainLayout）
src/layouts/BuyerLayout.vue          # 无需修改（使用 MainLayout）
src/layouts/ExpertLayout.vue         # 添加 AIAssistant 组件
src/layouts/BankLayout.vue           # 添加 AIAssistant 组件
src/layouts/AdminLayout.vue          # 添加 AIAssistant 组件
.env                                 # 添加 API 环境变量
package.json                         # 添加依赖
```

---

## Task 1: 安装依赖包

**文件:**
- 修改: `package.json`

**接口:**
- 无

- [ ] **Step 1: 安装 marked 和 dompurify**

```bash
cd "C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend"
npm install marked dompurify
```

预期输出: 成功安装包，添加到 package.json 和 package-lock.json

- [ ] **Step 2: 验证安装**

检查 `package.json` 的 dependencies 中包含:
```json
"marked": "^latest",
"dompurify": "^latest"
```

- [ ] **Step 3: 提交**

```bash
git add package.json package-lock.json
git commit -m "feat: install marked and dompurify for AI assistant"
```

---

## Task 2: 配置环境变量

**文件:**
- 修改: `.env`

**接口:**
- 无

- [ ] **Step 1: 添加 AI API 环境变量到 .env**

在 `.env` 文件末尾添加:
```bash
# DeepSeek API 配置
VITE_DEEPSEEK_API_KEY=OmQiPbQo4osbVNFNODwLX3gmM7SEwC3t2-DhH79ZbL8fgWIIxFtfaL-jbqdgHDZDERo5lb4zAk1TPP_PSFc4dA
VITE_DEEPSEEK_API_URL=https://www.sophnet.com/api/open-apis/v1/chat/completions
```

- [ ] **Step 2: 更新 .env.example**

在 `.env.example` 文件末尾添加:
```bash
# DeepSeek API 配置
VITE_DEEPSEEK_API_KEY=your_deepseek_api_key_here
VITE_DEEPSEEK_API_URL=https://www.sophnet.com/api/open-apis/v1/chat/completions
```

- [ ] **Step 3: 提交**

```bash
git add .env .env.example
git commit -m "feat: add DeepSeek API environment variables"
```

---

## Task 3: 创建 Markdown 工具模块

**文件:**
- 创建: `src/utils/markdown.js`

**接口:**
- 函数: `markdownToHtml(markdown: string) -> string`

- [ ] **Step 1: 创建 markdown.js 工具文件**

```javascript
/**
 * Markdown 处理工具
 */
import { marked } from 'marked'
import DOMPurify from 'dompurify'

/**
 * 将 Markdown 转换为安全的 HTML
 * @param {string} markdown - Markdown 文本
 * @returns {string} 安全的 HTML 字符串
 */
export function markdownToHtml(markdown) {
  if (!markdown) return ''

  // 配置 marked 选项
  marked.setOptions({
    breaks: true,      // 支持换行
    gfm: true,         // 支持 GitHub Flavored Markdown
    headerIds: false,  // 不生成标题 ID
    mangle: false      // 不转义邮箱
  })

  // 转换为 HTML
  const html = marked(markdown)

  // 净化 HTML，防止 XSS 攻击
  return DOMPurify.sanitize(html)
}

/**
 * 从 Markdown 中提取纯文本（用于预览）
 * @param {string} markdown - Markdown 文本
 * @returns {string} 纯文本
 */
export function markdownToText(markdown) {
  if (!markdown) return ''

  // 简单的 Markdown 到纯文本转换
  return markdown
    .replace(/#{1,6}\s+/g, '')        // 移除标题标记
    .replace(/\*\*(.+?)\*\*/g, '$1')  // 移除加粗标记
    .replace(/\*(.+?)\*/g, '$1')     // 移除斜体标记
    .replace(/`(.+?)`/g, '$1')        // 移除行内代码标记
    .replace(/\n/g, ' ')              // 换行转为空格
    .trim()
}
```

- [ ] **Step 2: 提交**

```bash
git add src/utils/markdown.js
git commit -m "feat: add markdown utility module"
```

---

## Task 4: 创建 AI Pinia Store

**文件:**
- 创建: `src/stores/ai.js`

**接口:**
- 状态: `messages`, `isOpen`, `isLoading`, `error`
- 方法: `toggleChat`, `openChat`, `closeChat`, `addMessage`, `clearMessages`, `setLoading`, `setError`

- [ ] **Step 1: 创建 ai.js store 文件**

```javascript
/**
 * AI 助手状态管理
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'

const STORAGE_KEY = 'ai_chat_history'
const MAX_MESSAGES = 50

export const useAIStore = defineStore('ai', () => {
  // 状态
  const isOpen = ref(false)
  const messages = ref([])
  const isLoading = ref(false)
  const error = ref(null)

  // 从 localStorage 加载历史记录
  const loadFromStorage = () => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored) {
        messages.value = JSON.parse(stored)
      }
    } catch (e) {
      console.error('加载聊天历史失败:', e)
      messages.value = []
    }
  }

  // 保存到 localStorage
  const saveToStorage = () => {
    try {
      // 只保存最近 MAX_MESSAGES 条消息
      const toSave = messages.value.slice(-MAX_MESSAGES)
      localStorage.setItem(STORAGE_KEY, JSON.stringify(toSave))
    } catch (e) {
      console.error('保存聊天历史失败:', e)
    }
  }

  // 初始化时加载历史记录
  loadFromStorage()

  // 切换聊天窗口开关
  const toggleChat = () => {
    isOpen.value = !isOpen.value
  }

  // 打开聊天窗口
  const openChat = () => {
    isOpen.value = true
  }

  // 关闭聊天窗口
  const closeChat = () => {
    isOpen.value = false
  }

  // 添加消息
  const addMessage = (message) => {
    messages.value.push({
      ...message,
      timestamp: new Date().toISOString()
    })

    // 限制消息数量
    if (messages.value.length > MAX_MESSAGES) {
      messages.value = messages.value.slice(-MAX_MESSAGES)
    }

    saveToStorage()
  }

  // 清空消息
  const clearMessages = () => {
    messages.value = []
    localStorage.removeItem(STORAGE_KEY)
  }

  // 设置加载状态
  const setLoading = (loading) => {
    isLoading.value = loading
  }

  // 设置错误
  const setError = (err) => {
    error.value = err
  }

  // 清除错误
  const clearError = () => {
    error.value = null
  }

  return {
    isOpen,
    messages,
    isLoading,
    error,
    toggleChat,
    openChat,
    closeChat,
    addMessage,
    clearMessages,
    setLoading,
    setError,
    clearError
  }
})
```

- [ ] **Step 2: 提交**

```bash
git add src/stores/ai.js
git commit -m "feat: add AI assistant Pinia store"
```

---

## Task 5: 创建 AI API 调用模块

**文件:**
- 创建: `src/api/ai.js`

**接口:**
- 函数: `sendMessage(messages: array) -> object`
- 消息格式: `{ role: 'system'|'user'|'assistant', content: string }`

- [ ] **Step 1: 创建 ai.js API 文件**

```javascript
/**
 * AI 助手 API 模块
 */

// 系统提示词
const SYSTEM_PROMPT = `你是农产品融销一体平台的智能助手，可以帮助用户解答以下问题：

1. 农业业务咨询：种植技术、病虫害防治、农产品销售、市场价格等
2. 平台操作指导：如何发布产品、如何下单、如何申请融资等
3. 融资服务：贷款产品、申请流程、还款方式等
4. 专家服务：如何预约专家、查看专家专长等
5. 其他相关问题

请用简洁、友好的语气回答，必要时引导用户使用平台相关功能。`

/**
 * 发送消息到 DeepSeek API
 * @param {Array} messages - 消息历史数组
 * @returns {Promise<string>} AI 回答内容
 */
export async function sendMessage(messages) {
  const apiKey = import.meta.env.VITE_DEEPSEEK_API_KEY
  const apiUrl = import.meta.env.VITE_DEEPSEEK_API_URL

  if (!apiKey) {
    throw new Error('API 密钥未配置')
  }

  if (!apiUrl) {
    throw new Error('API 地址未配置')
  }

  // 构建请求消息，添加系统提示词
  const requestMessages = [
    { role: 'system', content: SYSTEM_PROMPT },
    ...messages.map(msg => ({
      role: msg.role,
      content: msg.content
    }))
  ]

  try {
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey}`
      },
      body: JSON.stringify({
        model: 'DeepSeek-V4-Flash',
        messages: requestMessages,
        max_tokens: 2048,
        temperature: 0.7
      })
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.error?.message || `API 请求失败: ${response.status}`)
    }

    const data = await response.json()

    // 提取回答内容
    if (data.choices && data.choices.length > 0) {
      return data.choices[0].message.content
    }

    throw new Error('API 返回数据格式错误')
  } catch (error) {
    // 网络错误或其他错误
    if (error.name === 'TypeError' && error.message.includes('fetch')) {
      throw new Error('网络连接失败，请检查网络后重试')
    }
    throw error
  }
}

/**
 * 获取最近的消息历史（用于上下文）
 * @param {Array} allMessages - 所有消息
 * @param {number} maxCount - 最大消息数量
 * @returns {Array} 最近的消息
 */
export function getRecentMessages(allMessages, maxCount = 10) {
  // 只获取用户和助手的消息，不包括系统消息
  const chatMessages = allMessages.filter(msg => 
    msg.role === 'user' || msg.role === 'assistant'
  )

  // 返回最近的 maxCount 条消息
  return chatMessages.slice(-maxCount)
}
```

- [ ] **Step 2: 提交**

```bash
git add src/api/ai.js
git commit -m "feat: add AI API module"
```

---

## Task 6: 创建加载状态组件

**文件:**
- 创建: `src/components/AIAssistant/AILoading.vue`

**接口:**
- 无 props
- 无事件

- [ ] **Step 1: 创建 AILoading.vue 组件**

```vue
<template>
  <div class="ai-loading">
    <div class="loading-dots">
      <span class="dot"></span>
      <span class="dot"></span>
      <span class="dot"></span>
    </div>
    <p class="loading-text">正在思考...</p>
  </div>
</template>

<script setup>
// 加载状态组件，无额外逻辑
</script>

<style scoped>
.ai-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: #f5f5f5;
  border-radius: 12px;
  max-width: 200px;
}

.loading-dots {
  display: flex;
  gap: 4px;
}

.loading-dots .dot {
  width: 8px;
  height: 8px;
  background-color: #999;
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.loading-dots .dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.loading-text {
  margin: 0;
  font-size: 14px;
  color: #666;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add src/components/AIAssistant/AILoading.vue
git commit -m "feat: add AI loading component"
```

---

## Task 7: 创建单条消息组件

**文件:**
- 创建: `src/components/AIAssistant/AIMessage.vue`

**接口:**
- Props: `message: { role: string, content: string, timestamp: string }`

- [ ] **Step 1: 创建 AIMessage.vue 组件**

```vue
<template>
  <div :class="['ai-message', `ai-message--${message.role}`]">
    <div class="message-content">
      <div v-if="message.role === 'assistant'" class="message-avatar">
        <el-icon :size="20"><ChatDotRound /></el-icon>
      </div>
      <div class="message-bubble">
        <div
          v-if="message.role === 'assistant'"
          class="message-text"
          v-html="htmlContent"
        ></div>
        <div v-else class="message-text">
          {{ message.content }}
        </div>
        <div class="message-time">
          {{ formatTime(message.timestamp) }}
        </div>
      </div>
      <div v-if="message.role === 'user'" class="message-avatar">
        <el-icon :size="20"><User /></el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ChatDotRound, User } from '@element-plus/icons-vue'
import { markdownToHtml } from '@/utils/markdown'

const props = defineProps({
  message: {
    type: Object,
    required: true
  }
})

// 将 Markdown 转为 HTML（仅用于 AI 回答）
const htmlContent = computed(() => {
  if (props.message.role === 'assistant') {
    return markdownToHtml(props.message.content)
  }
  return props.message.content
})

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date

  // 如果是今天，显示时间
  if (diff < 86400000 && date.getDate() === now.getDate()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }

  // 否则显示日期
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}
</script>

<style scoped>
.ai-message {
  margin-bottom: 16px;
}

.ai-message--user {
  display: flex;
  justify-content: flex-end;
}

.ai-message--assistant {
  display: flex;
  justify-content: flex-start;
}

.message-content {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  max-width: 80%;
}

.message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-message--user .message-avatar {
  background-color: #2D5A3D;
  color: white;
}

.ai-message--assistant .message-avatar {
  background-color: #f0f0f0;
  color: #666;
}

.message-bubble {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ai-message--user .message-bubble {
  align-items: flex-end;
}

.ai-message--assistant .message-bubble {
  align-items: flex-start;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  max-width: 100%;
  word-wrap: break-word;
  line-height: 1.5;
}

.ai-message--user .message-text {
  background-color: #2D5A3D;
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-message--assistant .message-text {
  background-color: #f5f5f5;
  color: #333;
  border-bottom-left-radius: 4px;
}

/* Markdown 样式 */
.message-text :deep(p) {
  margin: 0 0 8px 0;
}

.message-text :deep(p:last-child) {
  margin-bottom: 0;
}

.message-text :deep(code) {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 0.9em;
}

.message-text :deep(pre) {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
}

.message-text :deep(pre code) {
  background-color: transparent;
  padding: 0;
}

.message-text :deep(ul),
.message-text :deep(ol) {
  margin: 8px 0;
  padding-left: 20px;
}

.message-text :deep(a) {
  color: #2D5A3D;
  text-decoration: underline;
}

.message-time {
  font-size: 12px;
  color: #999;
  padding: 0 4px;
}

.ai-message--user .message-time {
  text-align: right;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add src/components/AIAssistant/AIMessage.vue
git commit -m "feat: add AI message component"
```

---

## Task 8: 创建消息列表组件

**文件:**
- 创建: `src/components/AIAssistant/AIMessageList.vue`

**接口:**
- Props: `messages: array`, `isLoading: boolean`

- [ ] **Step 1: 创建 AIMessageList.vue 组件**

```vue
<template>
  <div class="ai-message-list" ref="listRef">
    <!-- 欢迎消息 -->
    <div v-if="messages.length === 0" class="welcome-message">
      <div class="welcome-icon">
        <el-icon :size="48"><ChatDotRound /></el-icon>
      </div>
      <h3>你好！我是农产品平台的 AI 助手</h3>
      <p>我可以帮你解答以下问题：</p>
      <ul class="suggested-questions">
        <li @click="sendSuggested('如何发布农产品？')">如何发布农产品？</li>
        <li @click="sendSuggested('申请融资需要什么条件？')">申请融资需要什么条件？</li>
        <li @click="sendSuggested('如何预约专家咨询？')">如何预约专家咨询？</li>
        <li @click="sendSuggested('常见的病虫害有哪些？')">常见的病虫害有哪些？</li>
      </ul>
    </div>

    <!-- 消息列表 -->
    <AIMessage
      v-for="message in messages"
      :key="message.timestamp"
      :message="message"
    />

    <!-- 加载状态 -->
    <AILoading v-if="isLoading" />

    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      <el-icon><WarningFilled /></el-icon>
      <span>{{ error }}</span>
      <el-button link type="primary" @click="retry">重试</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ChatDotRound, WarningFilled } from '@element-plus/icons-vue'
import AIMessage from './AIMessage.vue'
import AILoading from './AILoading.vue'

const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  isLoading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['send-message', 'retry'])

const listRef = ref(null)

// 发送建议问题
const sendSuggested = (question) => {
  emit('send-message', question)
}

// 重试
const retry = () => {
  emit('retry')
}

// 监听消息变化，自动滚动到底部
watch(() => props.messages, async () => {
  await nextTick()
  if (listRef.value) {
    listRef.value.scrollTop = listRef.value.scrollHeight
  }
}, { deep: true })

// 监听加载状态变化
watch(() => props.isLoading, async () => {
  await nextTick()
  if (listRef.value && props.isLoading) {
    listRef.value.scrollTop = listRef.value.scrollHeight
  }
})
</script>

<style scoped>
.ai-message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #fafafa;
}

.welcome-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 32px 16px;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2D5A3D 0%, #4a7c5f 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 24px;
}

.welcome-message h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.welcome-message p {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #666;
}

.suggested-questions {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
  max-width: 280px;
}

.suggested-questions li {
  padding: 12px 16px;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  color: #2D5A3D;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.suggested-questions li:hover {
  background-color: #2D5A3D;
  color: white;
  border-color: #2D5A3D;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: #fef2f2;
  border: 1px solid #fee;
  border-radius: 8px;
  color: #f56c6c;
  font-size: 14px;
}

.error-message .el-button {
  margin-left: auto;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add src/components/AIAssistant/AIMessageList.vue
git commit -m "feat: add AI message list component"
```

---

## Task 9: 创建输入区域组件

**文件:**
- 创建: `src/components/AIAssistant/AIInputArea.vue`

**接口:**
- Props: `disabled: boolean`
- 事件: `send: (content: string) => void`

- [ ] **Step 1: 创建 AIInputArea.vue 组件**

```vue
<template>
  <div class="ai-input-area">
    <div class="input-wrapper">
      <el-input
        v-model="inputText"
        type="textarea"
        :rows="1"
        :autosize="{ minRows: 1, maxRows: 4 }"
        placeholder="输入你的问题..."
        :disabled="disabled"
        @keydown.enter="handleEnter"
        @input="handleInput"
        :maxlength="2000"
        show-word-limit
      />
      <el-button
        type="primary"
        :icon="Promotion"
        :disabled="disabled || !canSend"
        @click="send"
        class="send-button"
      />
    </div>
    <div class="input-actions">
      <el-button
        link
        type="info"
        size="small"
        :disabled="disabled"
        @click="clearHistory"
      >
        清空对话
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Promotion } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send', 'clear-history'])

const inputText = ref('')

// 是否可以发送
const canSend = computed(() => {
  return inputText.value.trim().length > 0
})

// 处理输入
const handleInput = () => {
  // 自动调整高度由 el-input 的 autosize 处理
}

// 处理回车键
const handleEnter = (e) => {
  if (!e.shiftKey) {
    e.preventDefault()
    send()
  }
}

// 发送消息
const send = () => {
  if (!canSend.value) return

  const content = inputText.value.trim()
  if (content) {
    emit('send', content)
    inputText.value = ''
  }
}

// 清空历史
const clearHistory = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有对话记录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    emit('clear-history')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.ai-input-area {
  padding: 16px;
  border-top: 1px solid #e0e0e0;
  background-color: white;
}

.input-wrapper {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}

.input-wrapper :deep(.el-textarea) {
  flex: 1;
}

.input-wrapper :deep(.el-textarea__inner) {
  resize: none;
  border-radius: 20px;
  padding: 12px 16px;
  max-height: 120px;
}

.send-button {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  padding: 0;
  flex-shrink: 0;
}

.input-actions {
  margin-top: 8px;
  text-align: right;
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add src/components/AIAssistant/AIInputArea.vue
git commit -m "feat: add AI input area component"
```

---

## Task 10: 创建聊天窗口组件

**文件:**
- 创建: `src/components/AIAssistant/AIChatWindow.vue`

**接口:**
- 事件: `close: () => void`

- [ ] **Step 1: 创建 AIChatWindow.vue 组件**

```vue
<template>
  <div class="ai-chat-window">
    <!-- 头部 -->
    <div class="chat-header">
      <div class="header-title">
        <el-icon :size="20"><ChatDotRound /></el-icon>
        <span>AI 助手</span>
      </div>
      <div class="header-actions">
        <el-button
          link
          :icon="Delete"
          @click="handleClear"
        />
        <el-button
          link
          :icon="Close"
          @click="handleClose"
        />
      </div>
    </div>

    <!-- 消息列表 -->
    <AIMessageList
      :messages="messages"
      :is-loading="isLoading"
      :error="error"
      @send-message="handleSendMessage"
      @retry="handleRetry"
    />

    <!-- 输入区域 -->
    <AIInputArea
      :disabled="isLoading"
      @send="handleSend"
      @clear-history="handleClearHistory"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ChatDotRound, Close, Delete } from '@element-plus/icons-vue'
import { useAIStore } from '@/stores/ai'
import { sendMessage, getRecentMessages } from '@/api/ai'
import { ElMessage } from 'element-plus'
import AIMessageList from './AIMessageList.vue'
import AIInputArea from './AIInputArea.vue'

const emit = defineEmits(['close'])

const aiStore = useAIStore()

const messages = computed(() => aiStore.messages)
const isLoading = computed(() => aiStore.isLoading)
const error = computed(() => aiStore.error)

// 处理关闭
const handleClose = () => {
  emit('close')
}

// 处理发送
const handleSend = async (content) => {
  // 添加用户消息
  aiStore.addMessage({
    role: 'user',
    content
  })

  // 清除错误
  aiStore.clearError()

  // 设置加载状态
  aiStore.setLoading(true)

  try {
    // 获取最近的消息作为上下文
    const recentMessages = getRecentMessages(messages.value)

    // 调用 API
    const response = await sendMessage(recentMessages)

    // 添加 AI 回答
    aiStore.addMessage({
      role: 'assistant',
      content: response
    })
  } catch (err) {
    console.error('发送消息失败:', err)
    aiStore.setError(err.message || '发送失败，请重试')
    ElMessage.error(err.message || '发送失败，请重试')
  } finally {
    aiStore.setLoading(false)
  }
}

// 处理发送建议问题
const handleSendMessage = (question) => {
  handleSend(question)
}

// 重试
const handleRetry = () => {
  if (messages.value.length > 0) {
    // 获取最后一条用户消息重新发送
    const lastUserMessage = [...messages.value].reverse().find(m => m.role === 'user')
    if (lastUserMessage) {
      handleSend(lastUserMessage.content)
    }
  }
}

// 清空历史
const handleClearHistory = () => {
  aiStore.clearMessages()
  ElMessage.success('对话记录已清空')
}

// 清空按钮（和清空历史一样）
const handleClear = () => {
  aiStore.clearMessages()
  ElMessage.success('对话记录已清空')
}
</script>

<style scoped>
.ai-chat-window {
  position: fixed;
  right: 24px;
  bottom: 88px;
  width: 380px;
  height: 600px;
  max-height: calc(100vh - 120px);
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  z-index: 1000;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #2D5A3D;
  color: white;
  border-radius: 12px 12px 0 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.header-actions .el-button {
  color: white;
}

.header-actions .el-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add src/components/AIAssistant/AIChatWindow.vue
git commit -m "feat: add AI chat window component"
```

---

## Task 11: 创建浮动按钮组件

**文件:**
- 创建: `src/components/AIAssistant/AIChatButton.vue`

**接口:**
- Props: `isOpen: boolean`
- 事件: `toggle: () => void`

- [ ] **Step 1: 创建 AIChatButton.vue 组件**

```vue
<template>
  <div
    class="ai-chat-button"
    :class="{ 'ai-chat-button--open': isOpen }"
    @click="handleClick"
  >
    <transition name="icon-fade">
      <el-icon v-if="isOpen" :size="28" key="close">
        <Close />
      </el-icon>
      <el-icon v-else :size="28" key="chat">
        <ChatDotRound />
      </el-icon>
    </transition>
  </div>
</template>

<script setup>
import { ChatDotRound, Close } from '@element-plus/icons-vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle'])

const handleClick = () => {
  emit('toggle')
}
</script>

<style scoped>
.ai-chat-button {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2D5A3D 0%, #4a7c5f 100%);
  box-shadow: 0 4px 12px rgba(45, 90, 61, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1000;
  color: white;
}

.ai-chat-button:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(45, 90, 61, 0.5);
}

.ai-chat-button:active {
  transform: scale(0.95);
}

.ai-chat-button--open {
  background: linear-gradient(135deg, #666 0%, #888 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.icon-fade-enter-active,
.icon-fade-leave-active {
  transition: all 0.2s ease;
}

.icon-fade-enter-from {
  opacity: 0;
  transform: scale(0.5) rotate(-90deg);
}

.icon-fade-leave-to {
  opacity: 0;
  transform: scale(0.5) rotate(90deg);
}
</style>
```

- [ ] **Step 2: 提交**

```bash
git add src/components/AIAssistant/AIChatButton.vue
git commit -m "feat: add AI chat button component"
```

---

## Task 12: 创建主容器组件

**文件:**
- 创建: `src/components/AIAssistant/AIAssistant.vue`

**接口:**
- 无 props，无事件（通过 store 管理状态）

- [ ] **Step 1: 创建 AIAssistant.vue 主组件**

```vue
<template>
  <div class="ai-assistant">
    <AIChatButton
      :is-open="isOpen"
      @toggle="handleToggle"
    />
    <transition name="window-slide">
      <AIChatWindow
        v-if="isOpen"
        @close="handleClose"
      />
    </transition>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAIStore } from '@/stores/ai'
import AIChatButton from './AIChatButton.vue'
import AIChatWindow from './AIChatWindow.vue'

const aiStore = useAIStore()

const isOpen = computed(() => aiStore.isOpen)

const handleToggle = () => {
  aiStore.toggleChat()
}

const handleClose = () => {
  aiStore.closeChat()
}
</script>

<style scoped>
.ai-assistant {
  position: relative;
}

.window-slide-enter-active,
.window-slide-leave-active {
  transition: all 0.3s ease;
}

.window-slide-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.window-slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}
</style>
```

- [ ] **Step 2: 创建 index.js 导出文件**

```javascript
/**
 * AI 助手组件导出
 */
export { default as AIAssistant } from './AIAssistant.vue'
export { default as AIChatButton } from './AIChatButton.vue'
export { default as AIChatWindow } from './AIChatWindow.vue'
export { default as AIMessageList } from './AIMessageList.vue'
export { default as AIMessage } from './AIMessage.vue'
export { default as AIInputArea } from './AIInputArea.vue'
export { default as AILoading } from './AILoading.vue'
```

- [ ] **Step 3: 提交**

```bash
git add src/components/AIAssistant/AIAssistant.vue src/components/AIAssistant/index.js
git commit -m "feat: add AI assistant main component"
```

---

## Task 13: 在 MainLayout 中集成 AI 助手

**文件:**
- 修改: `src/layouts/MainLayout.vue`

**接口:**
- 无新增接口

- [ ] **Step 1: 修改 MainLayout.vue 添加 AI 助手**

找到 `<template>` 部分，在 `</div>`（最外层的 div）之前添加 AI 助手组件：

```vue
<template>
  <div class="main-layout">
    <AppHeader />
    <div class="layout-container">
      <AppSidebar
        :title="sidebarTitle"
        :menu-items="menuItems"
        @toggle-collapse="handleCollapse"
      />
      <main class="layout-main" :class="{ 'expanded': isCollapsed }">
        <div class="main-content">
          <router-view v-slot="{ Component, route }">
            <component :is="Component" :key="route.path" />
          </router-view>
        </div>
      </main>
    </div>
    <!-- AI 助手 -->
    <AIAssistant v-if="userStore.isLoggedIn()" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import AppHeader from './components/AppHeader.vue'
import AppSidebar from './components/AppSidebar.vue'
import { AIAssistant } from '@/components/AIAssistant'

const props = defineProps({
  sidebarTitle: {
    type: String,
    default: '工作台'
  },
  menuItems: {
    type: Array,
    default: () => []
  }
})

const route = useRoute()
const userStore = useUserStore()
const isCollapsed = ref(false)

const handleCollapse = (collapsed) => {
  isCollapsed.value = collapsed
}
</script>
```

- [ ] **Step 2: 提交**

```bash
git add src/layouts/MainLayout.vue
git commit -m "feat: integrate AI assistant into MainLayout"
```

---

## Task 14: 在独立布局中集成 AI 助手

**文件:**
- 修改: `src/layouts/ExpertLayout.vue`
- 修改: `src/layouts/BankLayout.vue`
- 修改: `src/layouts/AdminLayout.vue`

**接口:**
- 无新增接口

- [ ] **Step 1: 修改 ExpertLayout.vue**

```vue
<template>
  <div class="expert-layout">
    <!-- 现有内容 -->
    <AIAssistant v-if="userStore.isLoggedIn()" />
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { AIAssistant } from '@/components/AIAssistant'

const userStore = useUserStore()

// 现有代码保持不变
</script>
```

- [ ] **Step 2: 修改 BankLayout.vue**

```vue
<template>
  <div class="bank-layout">
    <!-- 现有内容 -->
    <AIAssistant v-if="userStore.isLoggedIn()" />
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { AIAssistant } from '@/components/AIAssistant'

const userStore = useUserStore()

// 现有代码保持不变
</script>
```

- [ ] **Step 3: 修改 AdminLayout.vue**

```vue
<template>
  <div class="admin-layout">
    <!-- 现有内容 -->
    <AIAssistant v-if="userStore.isLoggedIn()" />
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { AIAssistant } from '@/components/AIAssistant'

const userStore = useUserStore()

// 现有代码保持不变
</script>
```

- [ ] **Step 4: 提交**

```bash
git add src/layouts/ExpertLayout.vue src/layouts/BankLayout.vue src/layouts/AdminLayout.vue
git commit -m "feat: integrate AI assistant into independent layouts"
```

---

## Task 15: 测试和验证

**文件:**
- 无

**接口:**
- 无

- [ ] **Step 1: 启动开发服务器**

```bash
cd "C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend"
npm run dev
```

- [ ] **Step 2: 测试未登录状态**

访问首页 `http://localhost:5173/`，验证：
- 不显示 AI 助手按钮

- [ ] **Step 3: 测试已登录状态**

1. 登录任意角色账户
2. 验证右下角显示 AI 助手浮动按钮
3. 点击按钮，聊天窗口打开
4. 显示欢迎消息和建议问题

- [ ] **Step 4: 测试发送消息**

1. 输入问题并发送
2. 验证用户消息正确显示
3. 验证加载状态显示
4. 验证 AI 回答正确显示
5. 验证 Markdown 格式渲染

- [ ] **Step 5: 测试错误处理**

1. 临时修改 `.env` 中的 API 密钥为无效值
2. 发送消息，验证错误提示显示
3. 验证重试按钮工作

- [ ] **Step 6: 测试清空历史**

1. 发送几条消息
2. 点击清空对话按钮
3. 验证历史记录已清空
4. 刷新页面，验证历史记录未恢复（localStorage 已清空）

- [ ] **Step 7: 测试历史记录持久化**

1. 发送几条消息
2. 关闭聊天窗口
3. 刷新页面
4. 重新打开聊天窗口，验证消息历史保留

- [ ] **Step 8: 测试多角色**

分别用不同角色登录，验证 AI 助手都正常显示和工作：
- 农户
- 买家
- 专家
- 银行
- 管理员

- [ ] **Step 9: 测试建议问题**

点击欢迎消息中的建议问题，验证自动发送和回答

- [ ] **Step 10: 测试响应式布局**

在不同屏幕尺寸下测试：
- 桌面（>1200px）
- 平板（768px-1200px）
- 手机（<768px）

- [ ] **Step 11: 提交测试结果文档（可选）**

创建测试记录文件（可选）:
```bash
# 如果测试通过，提交最终版本
git add .
git commit -m "test: AI assistant feature testing completed"
```

---

## Task 16: 文档更新

**文件:**
- 修改: `README.md`（如果存在）

- [ ] **Step 1: 更新 README.md（如果存在）**

在 README.md 中添加 AI 助手功能说明：

```markdown
## AI 助手

平台内置 AI 助手功能，所有已登录用户都可以使用。

### 功能特点

- 智能问答：解答农业业务和平台操作问题
- 浮动聊天窗口：不影响当前页面操作
- 历史记录：自动保存最近 50 条对话
- Markdown 支持：AI 回答支持格式化显示

### 使用方法

登录后，点击右下角的聊天按钮即可开始对话。

### 环境配置

在 `.env` 文件中配置 DeepSeek API：

```bash
VITE_DEEPSEEK_API_KEY=your_api_key
VITE_DEEPSEEK_API_URL=https://www.sophnet.com/api/open-apis/v1/chat/completions
```
```

- [ ] **Step 2: 提交文档**

```bash
git add README.md
git commit -m "docs: add AI assistant feature documentation"
```

---

## 完成检查清单

在完成所有任务后，验证以下内容：

- [ ] 所有依赖已安装
- [ ] 环境变量已正确配置
- [ ] 所有组件已创建并正常工作
- [ ] AI API 调用正常
- [ ] 状态管理正常
- [ ] 所有布局已集成 AI 助手
- [ ] 登录/未登录权限控制正常
- [ ] 历史记录持久化正常
- [ ] 错误处理正常
- [ ] 响应式布局正常
- [ ] 所有角色都可以使用
- [ ] 代码已提交

---

## 后续优化建议

- 支持流式响应（SSE）
- 添加语音输入功能
- 支持多语言切换
- 添加常见问题快捷入口
- 支持导出聊天记录
- 添加使用统计和分析
