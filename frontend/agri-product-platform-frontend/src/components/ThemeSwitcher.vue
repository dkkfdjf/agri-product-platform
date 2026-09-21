<template>
  <el-switch
    v-model="isDark"
    class="theme-switch"
    :size="size"
    inline-prompt
    :active-icon="Moon"
    :inactive-icon="Sunny"
    @change="handleToggle"
  />
</template>

<script setup>
import { computed } from 'vue'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useThemeStore } from '@/stores/theme'

const props = defineProps({
  size: {
    type: String,
    default: 'default'
  }
})

const themeStore = useThemeStore()
const isDark = computed({
  get: () => themeStore.isDark,
  set: (value) => themeStore.setTheme(value ? 'dark' : 'light')
})

const handleToggle = () => {
  themeStore.toggleTheme()
}
</script>

<style scoped>
.theme-switch {
  --el-switch-on-color: var(--color-primary-dark);
  --el-switch-off-color: #E6E6E6;
  --el-switch-border-color: var(--color-border);
}

/* 深色模式下的开关样式 */
:root[data-theme='dark'] .theme-switch {
  --el-switch-off-color: #4A4A4A;
  --el-switch-border-color: #5A5A5A;
}

.theme-switch :deep(.el-switch__core) {
  border-color: var(--el-switch-border-color);
}

.theme-switch :deep(.el-switch__action) {
  background-color: white;
}

.theme-switch :deep(.el-icon) {
  color: white;
}
</style>
