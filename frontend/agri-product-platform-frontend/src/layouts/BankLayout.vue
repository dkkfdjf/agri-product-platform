<template>
  <MainLayout
    sidebar-title="银行工作台"
    :menu-items="bankMenuItems"
  />
</template>

<script setup>
import MainLayout from './MainLayout.vue'

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
    path: '/bank/matching',
    title: '智能匹配',
    icon: 'Connection'
  },
  {
    path: '/finance/bank',
    title: '融资管理',
    icon: 'Money'
  },
  {
    path: '/bank/products',
    title: '融资产品',
    icon: 'Coin'
  }
]
</script>
