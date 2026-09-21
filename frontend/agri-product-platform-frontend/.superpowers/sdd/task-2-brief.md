# Task 2 Brief: 配置环境变量

## 任务目标
在 .env 文件中添加 DeepSeek API 的环境变量配置。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 修改文件: `.env`, `.env.example`

## 执行步骤

### Step 1: 添加 AI API 环境变量到 .env

在 `.env` 文件末尾添加:
```bash
# DeepSeek API 配置
VITE_DEEPSEEK_API_KEY=OmQiPbQo4osbVNFNODwLX3gmM7SEwC3t2-DhH79ZbL8fgWIIxFtfaL-jbqdgHDZDERo5lb4zAk1TPP_PSFc4dA
VITE_DEEPSEEK_API_URL=https://www.sophnet.com/api/open-apis/v1/chat/completions
```

### Step 2: 更新 .env.example

在 `.env.example` 文件末尾添加:
```bash
# DeepSeek API 配置
VITE_DEEPSEEK_API_KEY=your_deepseek_api_key_here
VITE_DEEPSEEK_API_URL=https://www.sophnet.com/api/open-apis/v1/chat/completions
```

### Step 3: 提交
```bash
git add .env .env.example
git commit -m "feat: add DeepSeek API environment variables"
```

## 全局约束
- API 密钥: `OmQiPbQo4osbVNFNODwLX3gmM7SEwC3t2-DhH79ZbL8fgWIIxFtfaL-jbqdgHDZDERo5lb4zAk1TPP_PSFc4dA`
- API URL: `https://www.sophnet.com/api/open-apis/v1/chat/completions`

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-2-report.md`
