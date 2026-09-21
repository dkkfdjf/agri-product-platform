# Task 1 Brief: 安装依赖包

## 任务目标
安装 marked 和 dompurify npm 包，用于 Markdown 渲染和 HTML 安全净化。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 修改文件: `package.json`, `package-lock.json`

## 执行步骤

### Step 1: 安装依赖
```bash
cd "C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend"
npm install marked dompurify
```

### Step 2: 验证安装
检查 `package.json` 的 dependencies 中包含:
```json
"marked": "^latest",
"dompurify": "^latest"
```

### Step 3: 提交
```bash
git add package.json package-lock.json
git commit -m "feat: install marked and dompurify for AI assistant"
```

## 全局约束
- 遵循项目现有代码风格
- 使用 npm 而非其他包管理器

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-1-report.md`
