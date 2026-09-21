<template>
  <div
    class="ai-chat-button"
    :class="{ 'ai-chat-button--open': isOpen }"
    @click="handleClick"
  >
    <transition name="icon-fade">
      <el-icon v-if="isOpen" :size="28" key="close">
        <Close />
      </el-icon>
      <el-icon v-else :size="28" key="chat">
        <ChatDotRound />
      </el-icon>
    </transition>
  </div>
</template>

<script setup>
import { ChatDotRound, Close } from '@element-plus/icons-vue'

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle'])

const handleClick = () => {
  emit('toggle')
}
</script>

<style scoped>
.ai-chat-button {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  box-shadow: 0 4px 12px rgba(45, 90, 61, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1000;
  color: white;
}

.ai-chat-button:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(45, 90, 61, 0.5);
}

.ai-chat-button:active {
  transform: scale(0.95);
}

.ai-chat-button--open {
  background: linear-gradient(135deg, #666 0%, #888 100%);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

/* 深色模式 */
:root[data-theme='dark'] .ai-chat-button {
  box-shadow: 0 4px 12px rgba(45, 90, 61, 0.6);
}

:root[data-theme='dark'] .ai-chat-button:hover {
  box-shadow: 0 6px 16px rgba(45, 90, 61, 0.7);
}

/* 移动端优化 */
@media (max-width: 768px) {
  .ai-chat-button {
    right: 16px;
    bottom: 16px;
    width: 48px;
    height: 48px;
  }

  .ai-chat-button :deep(.el-icon) {
    font-size: 24px;
  }
}

@media (max-width: 480px) {
  .ai-chat-button {
    right: 12px;
    bottom: 12px;
    width: 44px;
    height: 44px;
  }

  .ai-chat-button :deep(.el-icon) {
    font-size: 22px;
  }
}

.icon-fade-enter-active,
.icon-fade-leave-active {
  transition: all 0.2s ease;
}

.icon-fade-enter-from {
  opacity: 0;
  transform: scale(0.5) rotate(-90deg);
}

.icon-fade-leave-to {
  opacity: 0;
  transform: scale(0.5) rotate(90deg);
}
</style>
