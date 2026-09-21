<template>
  <div class="ai-input-area">
    <div class="input-container">
      <el-input
        v-model="inputContent"
        type="textarea"
        :rows="3"
        :disabled="disabled"
        :maxlength="2000"
        show-word-limit
        placeholder="请输入您的问题..."
        @keydown="handleKeydown"
        class="message-input"
      />
    </div>
    <div class="button-group">
      <el-button
        type="primary"
        :disabled="disabled || !inputContent.trim()"
        :loading="disabled"
        @click="handleSend"
        class="send-button"
      >
        <el-icon class="send-icon">
          <Promotion />
        </el-icon>
        发送
      </el-button>
      <el-button
        :disabled="disabled"
        @click="handleClearHistory"
        class="clear-button"
      >
        清空对话
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Promotion } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const props = defineProps({
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['send', 'clear-history'])

const inputContent = ref('')

const handleSend = () => {
  const content = inputContent.value.trim()
  if (content && !props.disabled) {
    emit('send', content)
    inputContent.value = ''
  }
}

const handleKeydown = (event) => {
  // Enter to send, Shift+Enter for new line
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    handleSend()
  }
}

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
    emit('clear-history')
  } catch {
    // User cancelled
  }
}
</script>

<style scoped>
.ai-input-area {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  background-color: #FFFFFF;
  border-top: 1px solid #E5E7EB;
}

.input-container {
  width: 100%;
}

.message-input {
  width: 100%;
}

.message-input :deep(.el-textarea__inner) {
  resize: none;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  line-height: 1.5;
}

.message-input :deep(.el-textarea__inner):focus {
  border-color: var(--color-primary);
}

.button-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.send-button {
  min-width: 90px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.send-icon {
  font-size: 16px;
}

.clear-button {
  flex-shrink: 0;
}

@media (max-width: 768px) {
  .button-group {
    flex-direction: column-reverse;
    align-items: stretch;
  }

  .send-button {
    width: 100%;
  }

  .clear-button {
    width: 100%;
  }
}
</style>
