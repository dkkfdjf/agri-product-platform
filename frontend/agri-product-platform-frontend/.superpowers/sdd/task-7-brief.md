# Task 7 Brief: 创建单条消息组件

## 任务目标
创建 src/components/AIAssistant/AIMessage.vue 组件。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 创建文件: `src/components/AIAssistant/AIMessage.vue`

## 组件规格

### Props
- `message: Object` - 消息对象
  - `role: string` - 'user' 或 'assistant'
  - `content: string` - 消息内容
  - `timestamp: string` - 时间戳

### Events
- 无

### 功能
- 用户消息右对齐，绿色背景
- AI消息左对齐，灰色背景
- AI消息使用 Markdown 渲染
- 显示时间戳

### 依赖
- `@/utils/markdown` 的 markdownToHtml 函数
- Element Plus 图标: ChatDotRound, User

## 执行步骤

### Step 1: 创建 AIMessage.vue 组件
（完整代码见计划文件）

### Step 2: 提交
```bash
git add src/components/AIAssistant/AIMessage.vue
git commit -m "feat: add AI message component"
```

## 全局约束
- Vue 3 Composition API
- Element Plus

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-7-report.md`
