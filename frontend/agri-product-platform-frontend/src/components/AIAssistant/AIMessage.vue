<template>
  <div :class="['ai-message', message.role === 'user' ? 'user-message' : 'assistant-message']">
    <div class="message-header">
      <el-icon class="message-icon">
        <User v-if="message.role === 'user'" />
        <ChatDotRound v-else />
      </el-icon>
      <span class="message-sender">{{ message.role === 'user' ? '您' : 'AI 助手' }}</span>
      <span class="message-timestamp">{{ formatTimestamp(message.timestamp) }}</span>
    </div>
    <div class="message-content">
      <div
        v-if="message.role === 'assistant'"
        class="markdown-content"
        v-html="renderedContent"
      />
      <div v-else class="text-content">
        {{ message.content }}
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
    required: true,
    validator: (value) => {
      return ['role', 'content', 'timestamp'].every(key => key in value) &&
             ['user', 'assistant'].includes(value.role)
    }
  }
})

const renderedContent = computed(() => {
  return props.message.role === 'assistant' ? markdownToHtml(props.message.content) : ''
})

const formatTimestamp = (timestamp) => {
  if (!timestamp) return ''

  const date = new Date(timestamp)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return '刚刚'
  if (diffMins < 60) return `${diffMins} 分钟前`
  if (diffHours < 24) return `${diffHours} 小时前`
  if (diffDays < 7) return `${diffDays} 天前`

  return date.toLocaleDateString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.ai-message {
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-message {
  align-items: flex-end;
}

.assistant-message {
  align-items: flex-start;
}

.message-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--color-text-light);
}

.message-icon {
  font-size: 14px;
}

.message-sender {
  font-weight: 500;
}

.message-timestamp {
  margin-left: auto;
  opacity: 0.8;
}

.user-message .message-timestamp {
  margin-left: 0;
  margin-right: auto;
  order: -1;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  word-wrap: break-word;
}

.user-message .message-content {
  background-color: var(--color-primary);
  color: white;
  border-bottom-right-radius: 4px;
}

.assistant-message .message-content {
  background-color: #F3F4F6;
  color: var(--color-text);
  border-bottom-left-radius: 4px;
}

.markdown-content {
  line-height: 1.6;
}

.markdown-content :deep(p) {
  margin: 0 0 8px 0;
}

.markdown-content :deep(p:last-child) {
  margin-bottom: 0;
}

.markdown-content :deep(h1),
.markdown-content :deep(h2),
.markdown-content :deep(h3),
.markdown-content :deep(h4),
.markdown-content :deep(h5),
.markdown-content :deep(h6) {
  margin: 12px 0 8px 0;
  font-weight: 600;
}

.markdown-content :deep(h1) {
  font-size: 1.5em;
}

.markdown-content :deep(h2) {
  font-size: 1.3em;
}

.markdown-content :deep(h3) {
  font-size: 1.1em;
}

.markdown-content :deep(ul),
.markdown-content :deep(ol) {
  margin: 8px 0;
  padding-left: 20px;
}

.markdown-content :deep(li) {
  margin: 4px 0;
}

.markdown-content :deep(code) {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 2px 4px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.markdown-content :deep(pre) {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 12px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 8px 0;
}

.markdown-content :deep(pre code) {
  background-color: transparent;
  padding: 0;
}

.markdown-content :deep(blockquote) {
  border-left: 3px solid var(--color-primary);
  padding-left: 12px;
  margin: 8px 0;
  color: var(--color-text-light);
}

.markdown-content :deep(a) {
  color: var(--color-primary);
  text-decoration: underline;
}

.markdown-content :deep(table) {
  border-collapse: collapse;
  margin: 8px 0;
  width: 100%;
}

.markdown-content :deep(th),
.markdown-content :deep(td) {
  border: 1px solid #E5E7EB;
  padding: 8px 12px;
  text-align: left;
}

.markdown-content :deep(th) {
  background-color: rgba(0, 0, 0, 0.05);
  font-weight: 600;
}

.text-content {
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
