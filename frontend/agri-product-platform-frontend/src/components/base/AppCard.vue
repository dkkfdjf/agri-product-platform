<template>
  <div :class="cardClass" @click="handleClick">
    <!-- 卡片头部 -->
    <div v-if="$slots.header || header" class="card-header">
      <slot name="header">
        <h3 class="card-title">{{ header }}</h3>
      </slot>
    </div>

    <!-- 卡片内容 -->
    <div class="card-body">
      <slot></slot>
    </div>

    <!-- 卡片底部 -->
    <div v-if="$slots.footer" class="card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // 卡片标题
  header: {
    type: String,
    default: ''
  },
  // 是否可点击
  clickable: {
    type: Boolean,
    default: false
  },
  // 是否有阴影
  shadow: {
    type: String,
    default: 'md',
    validator: (value) => ['none', 'sm', 'md', 'lg', 'xl'].includes(value)
  },
  // 是否有边框
  bordered: {
    type: Boolean,
    default: false
  },
  // 卡片内边距
  padding: {
    type: String,
    default: 'md',
    validator: (value) => ['none', 'sm', 'md', 'lg'].includes(value)
  }
})

const emit = defineEmits(['click'])

const cardClass = computed(() => {
  return [
    'app-card',
    `app-card--shadow-${props.shadow}`,
    {
      'app-card--clickable': props.clickable,
      'app-card--bordered': props.bordered,
      `app-card--padding-${props.padding}`
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
.app-card {
  background: var(--color-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  transition: all var(--transition-base);
}

/* 阴影 */
.app-card--shadow-none {
  box-shadow: none;
}

.app-card--shadow-sm {
  box-shadow: var(--shadow-sm);
}

.app-card--shadow-md {
  box-shadow: var(--shadow-md);
}

.app-card--shadow-lg {
  box-shadow: var(--shadow-lg);
}

.app-card--shadow-xl {
  box-shadow: var(--shadow-xl);
}

/* 可点击 */
.app-card--clickable {
  cursor: pointer;
}

.app-card--clickable:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

/* 边框 */
.app-card--bordered {
  border: 1px solid var(--color-border);
}

/* 内边距 */
.app-card--padding-none .card-body {
  padding: 0;
}

.app-card--padding-sm .card-body {
  padding: var(--space-sm);
}

.app-card--padding-md .card-body {
  padding: var(--space-lg);
}

.app-card--padding-lg .card-body {
  padding: var(--space-xl);
}

/* 卡片头部 */
.card-header {
  padding: var(--space-lg) var(--space-lg) 0;
}

.card-title {
  font-size: var(--text-lg);
  font-weight: var(--font-semibold);
  color: var(--color-text);
  margin: 0;
}

/* 卡片内容 */
.card-body {
  padding: var(--space-lg);
}

/* 卡片底部 */
.card-footer {
  padding: 0 var(--space-lg) var(--space-lg);
  border-top: 1px solid var(--color-border);
  padding-top: var(--space-md);
}
</style>
