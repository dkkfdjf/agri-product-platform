<template>
  <button
    :class="buttonClass"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-icon">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" opacity="0.3"/>
        <path d="M12 2a10 10 0 0 1 10 10" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
      </svg>
    </span>
    <span v-if="$slots.prefix" class="button-prefix">
      <slot name="prefix"></slot>
    </span>
    <span class="button-content">
      <slot></slot>
    </span>
    <span v-if="$slots.suffix" class="button-suffix">
      <slot name="suffix"></slot>
    </span>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // 按钮类型
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary', 'outline', 'text', 'danger'].includes(value)
  },
  // 按钮大小
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 是否加载中
  loading: {
    type: Boolean,
    default: false
  },
  // 是否全宽
  block: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['click'])

const buttonClass = computed(() => {
  return [
    'app-button',
    `app-button--${props.type}`,
    `app-button--${props.size}`,
    {
      'app-button--block': props.block,
      'app-button--loading': props.loading
    }
  ]
})

const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped>
.app-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-family: var(--font-body);
  font-weight: var(--font-medium);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
  text-decoration: none;
}

.app-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 尺寸 */
.app-button--small {
  padding: 8px 16px;
  font-size: var(--text-sm);
  height: 32px;
}

.app-button--medium {
  padding: 10px 20px;
  font-size: var(--text-base);
  height: 40px;
}

.app-button--large {
  padding: 12px 24px;
  font-size: var(--text-lg);
  height: 48px;
}

/* 类型 */
.app-button--primary {
  color: var(--color-white);
  background: var(--color-primary);
}

.app-button--primary:hover:not(:disabled) {
  background: var(--color-primary-dark);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.app-button--secondary {
  color: var(--color-white);
  background: var(--color-secondary);
}

.app-button--secondary:hover:not(:disabled) {
  background: var(--color-secondary-light);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.app-button--outline {
  color: var(--color-primary);
  background: transparent;
  border: 1.5px solid var(--color-primary);
}

.app-button--outline:hover:not(:disabled) {
  background: rgba(45, 90, 61, 0.1);
}

.app-button--text {
  color: var(--color-primary);
  background: transparent;
  padding: 8px 12px;
}

.app-button--text:hover:not(:disabled) {
  background: rgba(45, 90, 61, 0.1);
}

.app-button--danger {
  color: var(--color-white);
  background: var(--color-danger);
}

.app-button--danger:hover:not(:disabled) {
  background: #9E4C2E;
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

/* 全宽 */
.app-button--block {
  width: 100%;
}

/* 加载状态 */
.app-button--loading {
  pointer-events: none;
}

.loading-icon {
  display: inline-flex;
  width: 16px;
  height: 16px;
  animation: spin 1s linear infinite;
}

.loading-icon svg {
  width: 100%;
  height: 100%;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 前缀和后缀 */
.button-prefix,
.button-suffix {
  display: flex;
  align-items: center;
}

.button-content {
  flex: 1;
}
</style>
