<template>
  <div :class="['ai-message', message.role === 'user' ? 'user-message' : 'assistant-message', `message-${message.id}`]">
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
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotRound, User } from '@element-plus/icons-vue'
import { markdownToHtml } from '@/utils/markdown'

const router = useRouter()

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
  if (props.message.role !== 'assistant') return ''

  let content = props.message.content

  // 识别快捷操作标记并转换为可点击的按钮
  const actions = {
    '【申请融资】': { name: '申请融资', route: '/finance/apply' },
    '【发布产品】': { name: '发布产品', route: '/farmer/products' },
    '【联系客服】': { name: '联系客服', action: 'contact' },
    '【查看订单】': { name: '查看订单', route: '/order' },
    '【购物车】': { name: '购物车', route: '/cart' },
    '【专家预约】': { name: '专家预约', route: '/expert-help' },
    '【个人中心】': { name: '个人中心', route: '/profile' },
    '【查看知识】': { name: '查看知识', route: '/knowledge' }
  }

  // 替换快捷操作标记为HTML按钮
  Object.keys(actions).forEach(key => {
    const action = actions[key]
    if (action.route) {
      content = content.replace(
        key,
        `<button class="quick-action-btn" data-route="${action.route}">${action.name}</button>`
      )
    } else if (action.action === 'contact') {
      content = content.replace(
        key,
        `<button class="quick-action-btn" data-action="contact">${action.name}</button>`
      )
    }
  })

  return markdownToHtml(content)
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

// 处理快捷操作按钮点击
onMounted(() => {
  const messageContent = document.querySelector(`.message-${props.message.id} .markdown-content`)
  if (messageContent) {
    messageContent.addEventListener('click', (e) => {
      if (e.target.classList.contains('quick-action-btn')) {
        const route = e.target.dataset.route
        const action = e.target.dataset.action

        if (route) {
          router.push(route)
          ElMessage.success('正在跳转...')
        } else if (action === 'contact') {
          ElMessage.info({
            message: '客服联系方式：\n电话：400-888-8888\n邮箱：service@agri-platform.com',
            duration: 5000
          })
        }
      }
    })
  }
})
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
  color: var(--color-text-tertiary);
  transition: color var(--transition-base, 250ms ease);
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
  color: var(--color-text-inverse, white);
  border-bottom-right-radius: 4px;
}

.assistant-message .message-content {
  background-color: var(--color-bg-tertiary, #F3F4F6);
  color: var(--color-text-primary);
  border-bottom-left-radius: 4px;
  transition: background-color var(--transition-base, 250ms ease);
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

/* 快捷操作按钮样式 */
.markdown-content :deep(.quick-action-btn) {
  display: inline-block;
  padding: 4px 12px;
  margin: 4px;
  background: linear-gradient(135deg, #2D5A3D 0%, #4a7c5f 100%);
  color: white;
  border: none;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(45, 90, 61, 0.2);
}

.markdown-content :deep(.quick-action-btn:hover) {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(45, 90, 61, 0.3);
}

.markdown-content :deep(.quick-action-btn:active) {
  transform: translateY(0);
}
</style>
