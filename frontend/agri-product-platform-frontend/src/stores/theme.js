/**
 * 主题状态管理
 * 支持深色/浅色模式切换
 */

import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

const THEME_KEY = 'app_theme'

export const useThemeStore = defineStore('theme', () => {
  // 状态
  const isDark = ref(loadThemeFromStorage())

  // 从localStorage加载主题
  function loadThemeFromStorage() {
    try {
      const stored = localStorage.getItem(THEME_KEY)
      if (stored) {
        return stored === 'dark'
      }
    } catch (err) {
      console.error('Failed to load theme from localStorage:', err)
    }
    // 默认使用浅色模式
    return false
  }

  // 保存主题到localStorage
  function saveThemeToStorage(theme) {
    try {
      localStorage.setItem(THEME_KEY, theme)
    } catch (err) {
      console.error('Failed to save theme to localStorage:', err)
    }
  }

  // 切换主题
  const toggleTheme = () => {
    isDark.value = !isDark.value
  }

  // 设置主题
  const setTheme = (theme) => {
    isDark.value = theme === 'dark'
  }

  // 应用主题到DOM
  const applyTheme = () => {
    const html = document.documentElement
    if (isDark.value) {
      html.classList.add('dark')
      html.setAttribute('data-theme', 'dark')
    } else {
      html.classList.remove('dark')
      html.setAttribute('data-theme', 'light')
    }
  }

  // 监听主题变化，自动应用
  watch(isDark, () => {
    applyTheme()
    saveThemeToStorage(isDark.value ? 'dark' : 'light')
  }, { immediate: true })

  return {
    isDark,
    toggleTheme,
    setTheme,
    applyTheme
  }
})
