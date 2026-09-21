# Task 5 Brief: 创建 AI API 调用模块

## 任务目标
创建 src/api/ai.js 文件，实现调用 DeepSeek API 的功能。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 创建文件: `src/api/ai.js`

## 接口定义

### 函数
- `sendMessage(messages: Array) -> Promise<string>`: 发送消息到 DeepSeek API
- `getRecentMessages(allMessages: Array, maxCount: number) -> Array`: 获取最近的消息历史

### 消息格式
```javascript
{ role: 'system'|'user'|'assistant', content: string }
```

### API 配置
- URL: import.meta.env.VITE_DEEPSEEK_API_URL
- KEY: import.meta.env.VITE_DEEPSEEK_API_KEY
- 模型: 'DeepSeek-V4-Flash'
- max_tokens: 2048
- temperature: 0.7

### 系统提示词
```
你是农产品融销一体平台的智能助手，可以帮助用户解答以下问题：

1. 农业业务咨询：种植技术、病虫害防治、农产品销售、市场价格等
2. 平台操作指导：如何发布产品、如何下单、如何申请融资等
3. 融资服务：贷款产品、申请流程、还款方式等
4. 专家服务：如何预约专家、查看专家专长等
5. 其他相关问题

请用简洁、友好的语气回答，必要时引导用户使用平台相关功能。
```

## 执行步骤

### Step 1: 创建 ai.js API 文件
（完整代码见计划文件）

### Step 2: 提交
```bash
git add src/api/ai.js
git commit -m "feat: add AI API module"
```

## 全局约束
- 使用 fetch API 调用
- 环境变量: VITE_DEEPSEEK_API_KEY, VITE_DEEPSEEK_API_URL

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-5-report.md`
