<template>
  <div class="ai-message-list">
    <!-- 欢迎消息（无消息时） -->
    <div v-if="!messages || messages.length === 0" class="welcome-message">
      <el-icon class="welcome-icon"><ChatDotRound /></el-icon>
      <h3>欢迎来到智能助手</h3>
      <p>我可以帮您解答农产品平台相关问题</p>
      <div class="suggested-questions">
        <el-button
          v-for="(question, index) in suggestedQuestions"
          :key="index"
          size="small"
          @click="handleSendMessage(question)"
        >
          {{ question }}
        </el-button>
      </div>
    </div>

    <!-- 消息列表 -->
    <transition-group
      v-else
      name="message-fade"
      tag="div"
      class="messages-container"
    >
      <AIMessage
        v-for="message in messages"
        :key="message.id"
        :message="message"
        class="message-item"
      />
    </transition-group>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <AILoading />
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      <el-icon class="error-icon"><WarningFilled /></el-icon>
      <span>{{ error }}</span>
      <el-button size="small" type="primary" @click="handleRetry">
        重试
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ChatDotRound, WarningFilled } from '@element-plus/icons-vue'
import AIMessage from './AIMessage.vue'
import AILoading from './AILoading.vue'

// Props
const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  isLoading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  }
})

// Events
const emit = defineEmits(['send-message', 'retry'])

// 建议问题列表
const suggestedQuestions = ref([
  '如何发布农产品？',
  '平台有哪些功能？',
  '如何联系客服？',
  '农产品如何分类？'
])

// 处理发送建议问题
const handleSendMessage = (question) => {
  emit('send-message', question)
}

// 处理重试
const handleRetry = () => {
  emit('retry')
}

// 自动滚动到底部
const scroll_to_bottom = () => {
  nextTick(() => {
    const container = document.querySelector('.messages-container')
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动
watch(() => props.messages, () => {
  scroll_to_bottom()
}, { deep: true })

// 监听加载状态变化，自动滚动
watch(() => props.isLoading, () => {
  scroll_to_bottom()
})
</script>

<style scoped>
.ai-message-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  position: relative;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
  text-align: center;
}

.welcome-icon {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 20px;
}

.welcome-message h3 {
  font-size: 24px;
  margin: 0 0 10px 0;
  color: #303133;
}

.welcome-message p {
  font-size: 14px;
  color: #606266;
  margin: 0 0 30px 0;
}

.suggested-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  max-width: 500px;
}

/* 消息容器 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  scroll-behavior: smooth;
}

.message-item {
  margin-bottom: 16px;
}

/* 消息动画 */
.message-fade-enter-active,
.message-fade-leave-active {
  transition: all 0.3s ease;
}

.message-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.message-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 加载容器 */
.loading-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

/* 错误消息 */
.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  background-color: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 4px;
  margin: 10px 20px;
  color: #f56c6c;
}

.error-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.error-message span {
  flex: 1;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-message h3 {
    font-size: 20px;
  }

  .welcome-icon {
    font-size: 48px;
  }

  .suggested-questions {
    flex-direction: column;
    align-items: stretch;
  }

  .messages-container {
    padding: 15px;
  }

  .message-item {
    margin-bottom: 12px;
  }
}
</style>
