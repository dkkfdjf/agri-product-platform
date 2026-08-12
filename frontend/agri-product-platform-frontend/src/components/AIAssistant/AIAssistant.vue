<template>
  <div class="ai-assistant">
    <AIChatButton
      :is-open="isOpen"
      @toggle="handleToggle"
    />
    <transition name="window-slide">
      <AIChatWindow
        v-if="isOpen"
        @close="handleClose"
      />
    </transition>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useAiStore } from '@/stores/ai'
import AIChatButton from './AIChatButton.vue'
import AIChatWindow from './AIChatWindow.vue'

const aiStore = useAiStore()

const isOpen = computed(() => aiStore.isOpen)

const handleToggle = () => {
  aiStore.toggleChat()
}

const handleClose = () => {
  aiStore.closeChat()
}
</script>

<style scoped>
.ai-assistant {
  position: relative;
}

.window-slide-enter-active,
.window-slide-leave-active {
  transition: all 0.3s ease;
}

.window-slide-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.window-slide-leave-to {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}
</style>
