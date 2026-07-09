<template>
  <div :class="cardClass" @click="handleClick">
    <div class="stat-icon" :style="{ color: color, background: bgColor }">
      <slot name="icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="12" cy="12" r="10"/>
        </svg>
      </slot>
    </div>
    <div class="stat-content">
      <div class="stat-value">{{ value }}</div>
      <div class="stat-label">{{ label }}</div>
    </div>
    <div v-if="showArrow" class="stat-arrow">
      <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
        <path d="M6 3l5 5-5 5"/>
      </svg>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // 统计数值
  value: {
    type: [String, Number],
    required: true
  },
  // 统计标签
  label: {
    type: String,
    required: true
  },
  // 图标颜色
  color: {
    type: String,
    default: 'var(--color-primary)'
  },
  // 图标背景色
  bgColor: {
    type: String,
    default: 'rgba(45, 90, 61, 0.1)'
  },
  // 是否可点击
  clickable: {
    type: Boolean,
    default: false
  },
  // 是否显示箭头
  showArrow: {
    type: Boolean,
    default: true
  },
  // 卡片类型（预设颜色）
  type: {
    type: String,
    default: '',
    validator: (value) => ['', 'primary', 'secondary', 'success', 'warning', 'danger'].includes(value)
  }
})

const emit = defineEmits(['click'])

const cardClass = computed(() => {
  return [
    'stat-card',
    {
      'stat-card--clickable': props.clickable,
      [`stat-card--${props.type}`]: props.type
    }
  ]
})

const handleClick = (event) => {
  if (props.clickable) {
    emit('click', event)
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-lg);
  background: var(--color-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
}

.stat-card--clickable {
  cursor: pointer;
}

.stat-card--clickable:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--color-text);
  line-height: 1;
  margin-bottom: var(--space-xs);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

.stat-arrow {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  color: var(--color-text-light);
  opacity: 0.5;
}

/* 预设类型 */
.stat-card--primary .stat-icon {
  color: var(--color-primary);
  background: rgba(45, 90, 61, 0.1);
}

.stat-card--secondary .stat-icon {
  color: var(--color-secondary);
  background: rgba(201, 166, 97, 0.15);
}

.stat-card--success .stat-icon {
  color: var(--color-success);
  background: rgba(74, 124, 89, 0.1);
}

.stat-card--warning .stat-icon {
  color: var(--color-warning);
  background: rgba(201, 166, 97, 0.15);
}

.stat-card--danger .stat-icon {
  color: var(--color-danger);
  background: rgba(184, 92, 56, 0.1);
}

@media (max-width: 768px) {
  .stat-card {
    padding: var(--space-md);
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }

  .stat-icon svg {
    width: 24px;
    height: 24px;
  }

  .stat-value {
    font-size: var(--text-2xl);
  }
}
</style>
