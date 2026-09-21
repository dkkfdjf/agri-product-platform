# Task 3 Brief: 创建 Markdown 工具模块

## 任务目标
创建 src/utils/markdown.js 工具文件，提供 Markdown 转 HTML 和纯文本的功能。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 创建文件: `src/utils/markdown.js`

## 执行步骤

### Step 1: 创建 markdown.js 工具文件

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

### Step 2: 提交
```bash
git add src/utils/markdown.js
git commit -m "feat: add markdown utility module"
```

## 接口定义
- `markdownToHtml(markdown: string) -> string`: 将 Markdown 转换为安全的 HTML
- `markdownToText(markdown: string) -> string`: 提取纯文本

## 全局约束
- 使用 marked 和 dompurify 包（已在 Task 1 安装）
- 遵循项目现有代码风格（单引号、2空格缩进）

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-3-report.md`
