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
