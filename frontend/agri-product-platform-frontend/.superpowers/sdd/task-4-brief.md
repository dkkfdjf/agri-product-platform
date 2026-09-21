# Task 4 Brief: 创建 AI Pinia Store

## 任务目标
创建 src/stores/ai.js 文件，实现 AI 助手的状态管理。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 创建文件: `src/stores/ai.js`

## 接口定义

### 状态
- `isOpen: Ref<boolean>` - 聊天窗口是否打开
- `messages: Ref<Array>` - 消息列表
- `isLoading: Ref<boolean>` - 是否正在加载
- `error: Ref<string|null>` - 错误信息

### 方法
- `toggleChat()` - 切换聊天窗口开关
- `openChat()` - 打开聊天窗口
- `closeChat()` - 关闭聊天窗口
- `addMessage(message)` - 添加消息
- `clearMessages()` - 清空消息
- `setLoading(loading)` - 设置加载状态
- `setError(err)` - 设置错误
- `clearError()` - 清除错误

### 持久化
- 使用 localStorage 存储历史记录
- 保存最近 50 条消息
- STORAGE_KEY: 'ai_chat_history'
- MAX_MESSAGES: 50

## 执行步骤

### Step 1: 创建 ai.js store 文件
（完整代码见计划文件）

### Step 2: 提交
```bash
git add src/stores/ai.js
git commit -m "feat: add AI assistant Pinia store"
```

## 全局约束
- 使用 Pinia defineStore 和 Composition API
- 遵循项目现有代码风格

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-4-report.md`
