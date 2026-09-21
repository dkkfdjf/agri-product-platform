<template>
  <div class="ai-chat-window">
    <!-- 头部 -->
    <div class="chat-window-header">
      <div class="header-left">
        <el-icon class="header-icon"><ChatDotRound /></el-icon>
        <h2 class="header-title">AI 智能助手</h2>
      </div>
      <div class="header-right">
        <el-button
          text
          :icon="Delete"
          class="header-button"
          title="清空对话"
          @click="handleClearHistory"
        />
        <el-button
          text
          :icon="Close"
          class="header-button close-button"
          title="关闭"
          @click="handleClose"
        />
      </div>
    </div>

    <!-- 消息列表区域 -->
    <div class="chat-window-body">
      <AIMessageList
        :messages="aiStore.messages"
        :is-loading="aiStore.isLoading"
        :error="aiStore.error"
        @send-message="handleSendMessage"
        @retry="handleRetry"
      />
    </div>

    <!-- 输入区域 -->
    <div class="chat-window-footer">
      <AIInputArea
        :disabled="aiStore.isLoading"
        @send="handleSend"
        @clear-history="handleClearHistory"
      />
    </div>
  </div>
</template>

<script setup>
import { ChatDotRound, Close, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import AIMessageList from './AIMessageList.vue'
import AIInputArea from './AIInputArea.vue'
import { useAiStore } from '@/stores/ai'
import { sendMessage, getRecentMessages } from '@/api/ai'

// Store
const aiStore = useAiStore()

// Events
const emit = defineEmits(['close'])

// 关闭窗口
const handleClose = () => {
  emit('close')
}

// 发送消息
const handleSend = async (content) => {
  if (!content || !content.trim()) {
    return
  }

  // 添加用户消息
  aiStore.addMessage({
    role: 'user',
    content: content.trim()
  })

  // 清除之前的错误
  aiStore.clearError()

  // 设置加载状态
  aiStore.setLoading(true)

  try {
    // 构建消息历史（转换为API格式）
    const messageHistory = aiStore.messages
      .filter(msg => msg.role && msg.content)
      .map(msg => ({
        role: msg.role === 'assistant' ? 'assistant' : 'user',
        content: msg.content
      }))

    // 获取最近的消息
    const recentMessages = getRecentMessages(messageHistory, 10)

    // 调用AI API
    const response = await sendMessage(recentMessages)

    // 添加AI回复
    aiStore.addMessage({
      role: 'assistant',
      content: response
    })

    // 显示成功提示
    ElMessage.success('消息发送成功')
  } catch (error) {
    console.error('发送消息失败:', error)

    // 设置错误状态
    aiStore.setError(error.message || '发送失败，请重试')

    // 显示错误提示
    ElMessage.error('发送失败: ' + (error.message || '未知错误'))
  } finally {
    // 清除加载状态
    aiStore.setLoading(false)
  }
}

// 处理发送建议问题
const handleSendMessage = (question) => {
  handleSend(question)
}

// 处理重试
const handleRetry = async () => {
  // 获取最后一条用户消息
  const lastUserMessage = [...aiStore.messages]
    .reverse()
    .find(msg => msg.role === 'user')

  if (lastUserMessage) {
    // 清除错误，重新发送
    aiStore.clearError()
    await handleSend(lastUserMessage.content)
  }
}

// 清空历史
const handleClearHistory = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有对话记录吗？此操作不可恢复。',
      '清空对话',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 清空消息
    aiStore.clearMessages()

    // 清除错误
    aiStore.clearError()

    ElMessage.success('对话记录已清空')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.ai-chat-window {
  position: fixed;
  right: 24px;
  bottom: 90px;
  width: 380px;
  max-width: calc(100vw - 48px);
  height: 600px;
  max-height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  background-color: var(--color-bg-primary, #FFFFFF);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: background-color var(--transition-base, 250ms ease);
  z-index: 1000;
}

/* 深色模式下的阴影 */
:root[data-theme='dark'] .ai-chat-window {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
}

/* 头部 */
.chat-window-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
}

.header-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  display: flex;
  gap: 8px;
}

.header-button {
  color: rgba(255, 255, 255, 0.9);
  font-size: 18px;
  padding: 4px;
}

.header-button:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.close-button:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

/* 主体区域 */
.chat-window-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background-color: var(--color-bg-secondary, #F9FAFB);
  transition: background-color var(--transition-base, 250ms ease);
}

/* 底部输入区域 */
.chat-window-footer {
  background-color: var(--color-bg-primary, #FFFFFF);
  border-top: 1px solid var(--color-border, #E5E7EB);
  transition: background-color var(--transition-base, 250ms ease);
}

/* 响应式 */
@media (max-width: 768px) {
  .ai-chat-window {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    height: 100vh;
    border-radius: 0;
    z-index: var(--z-modal, 1050);
  }

  .chat-window-header {
    padding: 12px 16px;
  }

  .header-title {
    font-size: 16px;
  }

  .header-icon {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .header-left {
    gap: 8px;
  }

  .header-title {
    font-size: 14px;
  }

  .header-button {
    font-size: 16px;
    padding: 4px;
  }

  .chat-window-body {
    padding: 0;
  }
}
</style>
