/**
 * AI助手状态管理
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'

const STORAGE_KEY = 'ai_chat_history'
const MAX_MESSAGES = 50

export const useAiStore = defineStore('ai', () => {
  // 状态
  const isOpen = ref(false)
  const messages = ref(loadMessagesFromStorage())
  const isLoading = ref(false)
  const error = ref(null)

  // 从localStorage加载消息
  function loadMessagesFromStorage() {
    try {
      const stored = localStorage.getItem(STORAGE_KEY)
      if (stored) {
        return JSON.parse(stored)
      }
    } catch (err) {
      console.error('Failed to load messages from localStorage:', err)
    }
    return []
  }

  // 保存消息到localStorage
  function saveMessagesToStorage(msgs) {
    try {
      // 只保留最近MAX_MESSAGES条消息
      const toSave = msgs.slice(-MAX_MESSAGES)
      localStorage.setItem(STORAGE_KEY, JSON.stringify(toSave))
    } catch (err) {
      console.error('Failed to save messages to localStorage:', err)
    }
  }

  // 切换聊天窗口开关
  const toggleChat = () => {
    isOpen.value = !isOpen.value
  }

  // 打开聊天窗口
  const openChat = () => {
    isOpen.value = true
  }

  // 关闭聊天窗口
  const closeChat = () => {
    isOpen.value = false
  }

  // 添加消息
  const addMessage = (message) => {
    if (!message) return

    const newMessage = {
      id: Date.now(),
      timestamp: new Date().toISOString(),
      ...message
    }

    messages.value.push(newMessage)

    // 保存到localStorage
    saveMessagesToStorage(messages.value)
  }

  // 清空消息
  const clearMessages = () => {
    messages.value = []
    localStorage.removeItem(STORAGE_KEY)
  }

  // 设置加载状态
  const setLoading = (loading) => {
    isLoading.value = loading
  }

  // 设置错误
  const setError = (err) => {
    error.value = err
  }

  // 清除错误
  const clearError = () => {
    error.value = null
  }

  // 获取消息数量
  const getMessageCount = () => {
    return messages.value.length
  }

  // 获取最后一条消息
  const getLastMessage = () => {
    return messages.value[messages.value.length - 1] || null
  }

  return {
    isOpen,
    messages,
    isLoading,
    error,
    toggleChat,
    openChat,
    closeChat,
    addMessage,
    clearMessages,
    setLoading,
    setError,
    clearError,
    getMessageCount,
    getLastMessage
  }
})
