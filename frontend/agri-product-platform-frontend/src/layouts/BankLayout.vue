<template>
  <MainLayout
    sidebar-title="银行工作台"
    :menu-items="bankMenuItems"
  />
  <AIAssistant v-if="userStore.isLoggedIn()" />
</template>

<script setup>
import MainLayout from './MainLayout.vue'
import { AIAssistant } from '@/components/AIAssistant'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const bankMenuItems = [
  {
    path: '/bank/dashboard',
    title: '数据概览',
    icon: 'DataAnalysis'
  },
  {
    path: '/bank/approvals',
    title: '融资审批',
    icon: 'DocumentChecked'
  },
  {
    path: '/bank/repayments',
    title: '还款审核',
    icon: 'Wallet'
  },
  {
    path: '/bank/products',
    title: '融资产品',
    icon: 'Coin'
  },
  {
    path: '/bank/matching',
    title: '智能匹配',
    icon: 'Connection'
  }
]
</script>
