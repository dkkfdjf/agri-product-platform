/**
 * 用户状态管理
 */

import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const role = ref(localStorage.getItem('role') || '')

  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 设置角色
  const setRole = (newRole) => {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('role')
  }

  // 是否已登录
  const isLoggedIn = () => {
    return !!token.value
  }

  // 检查角色权限
  const hasRole = (allowedRoles) => {
    if (Array.isArray(allowedRoles)) {
      return allowedRoles.includes(role.value)
    }
    return role.value === allowedRoles
  }

  return {
    token,
    userInfo,
    role,
    setToken,
    setUserInfo,
    setRole,
    logout,
    isLoggedIn,
    hasRole
  }
})
