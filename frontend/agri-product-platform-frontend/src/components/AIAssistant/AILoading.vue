<template>
  <div class="ai-loading">
    <div class="ai-loading__dots">
      <span class="ai-loading__dot"></span>
      <span class="ai-loading__dot"></span>
      <span class="ai-loading__dot"></span>
    </div>
    <div class="ai-loading__text">正在思考...</div>
  </div>
</template>

<script setup>
/**
 * AI Loading Component
 * - Displays bouncing animation dots while AI is thinking
 * - Shows "正在思考..." text
 */
// No props or events needed for this component
</script>

<style scoped>
.ai-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  gap: 12px;
}

.ai-loading__dots {
  display: flex;
  gap: 8px;
  align-items: center;
}

.ai-loading__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #409eff;
  animation: bounce 1.4s infinite ease-in-out both;
}

.ai-loading__dot:nth-child(1) {
  animation-delay: -0.32s;
}

.ai-loading__dot:nth-child(2) {
  animation-delay: -0.16s;
}

.ai-loading__dot:nth-child(3) {
  animation-delay: 0s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.ai-loading__text {
  font-size: 14px;
  color: #909399;
  text-align: center;
}
</style>
