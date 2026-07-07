<template>
  <div class="finance-products-page">
    <!-- 导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="goTo('/')">
          <el-icon :size="28" color="#fff"><Grid /></el-icon>
          <span>农产品融销平台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          class="nav-menu"
          router
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item index="/finance-products">融资服务</el-menu-item>
          <el-menu-item index="/market">农产品交易</el-menu-item>
          <el-menu-item index="/expert-help">专家助力</el-menu-item>
        </el-menu>
        <div class="header-actions">
          <template v-if="!userStore.isLoggedIn()">
            <el-button type="success" plain @click="goToLogin">登录</el-button>
          </template>
          <template v-else>
            <el-dropdown @command="handleUserCommand">
              <span class="user-name">
                <el-icon><User /></el-icon>
                {{ getUserDisplayName() }}
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="getDashboardPath()">
                    <el-icon><Odometer /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </el-header>

    <!-- 主内容 -->
    <el-main class="main">
      <h2 class="page-title">融资产品</h2>

      <el-row :gutter="20">
        <el-col :span="8" v-for="product in productList" :key="product.productId">
          <el-card class="finance-card" shadow="hover">
            <div class="card-header">
              <el-icon :size="40" color="#4CAF50"><Wallet /></el-icon>
              <div>
                <h3>{{ product.productName }}</h3>
                <p class="bank">{{ product.bankName }}</p>
              </div>
            </div>
            <el-divider />
            <div class="card-body">
              <div class="info-item">
                <span>最高额度</span>
                <span class="value">¥{{ product.maxAmount }}</span>
              </div>
              <div class="info-item">
                <span>年利率</span>
                <span class="value">{{ product.rate }}%</span>
              </div>
              <div class="info-item">
                <span>最长期限</span>
                <span class="value">{{ product.maxTerm }}个月</span>
              </div>
            </div>
            <el-divider />
            <div class="card-footer">
              <el-button type="primary" v-if="userStore.role === 'farmer'" @click="handleApply(product)">
                立即申请
              </el-button>
              <el-button @click="handleViewDetail(product)">
                查看详情
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-main>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="产品详情" width="600px">
      <div v-if="currentProduct" class="product-detail">
        <h2>{{ currentProduct.productName }}</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="银行">{{ currentProduct.bankName }}</el-descriptions-item>
          <el-descriptions-item label="年利率">{{ currentProduct.rate }}%</el-descriptions-item>
          <el-descriptions-item label="最高额度">¥{{ currentProduct.maxAmount }}</el-descriptions-item>
          <el-descriptions-item label="最长期限">{{ currentProduct.maxTerm }}个月</el-descriptions-item>
          <el-descriptions-item label="产品描述" span="2">{{ currentProduct.description }}</el-descriptions-item>
          <el-descriptions-item label="申请条件" span="2">{{ currentProduct.conditions }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer v-if="userStore.role === 'farmer'">
        <el-button type="primary" @click="handleApply(currentProduct)">立即申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const productList = ref([])
const detailVisible = ref(false)
const currentProduct = ref(null)

// 获取用户显示名称
const getUserDisplayName = () => {
  const userInfo = userStore.userInfo
  if (!userInfo) return ''
  const role = userStore.role
  switch (role) {
    case 'farmer':
      return userInfo.farmName || userInfo.userName
    case 'expert':
      return userInfo.expertName || userInfo.userName
    case 'bank':
      return userInfo.bankName || userInfo.userName
    case 'admin':
      return '管理员'
    default:
      return userInfo.userName
  }
}

// 获取用户主页路径
const getDashboardPath = () => {
  const role = userStore.role
  switch (role) {
    case 'farmer':
      return '/farmer/dashboard'
    case 'expert':
      return '/expert/dashboard'
    case 'bank':
      return '/bank/dashboard'
    case 'admin':
      return '/admin/dashboard'
    default:
      return '/'
  }
}

// 处理用户命令
const handleUserCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    location.reload()
  } else {
    router.push(command)
  }
}

// 跳转登录
const goToLogin = () => {
  router.push('/login')
}

// 页面跳转
const goTo = (path) => {
  router.push(path)
}

// 加载产品列表
const loadProductList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await financeApi.productList()

    // 模拟数据
    productList.value = [
      {
        productId: 1,
        productName: '农业小额贷款',
        bankName: '中国农业银行',
        maxAmount: 50000,
        rate: 4.35,
        maxTerm: 12,
        description: '面向农户的小额信用贷款，手续简便，放款快速',
        conditions: '具有农村户籍，从事农业生产，信用良好'
      },
      {
        productId: 2,
        productName: '农业生产贷款',
        bankName: '中国农业银行',
        maxAmount: 200000,
        rate: 4.75,
        maxTerm: 36,
        description: '支持农业生产经营的中期贷款，可用于购买农资、扩大生产',
        conditions: '从事农业生产满1年，有稳定收入来源'
      },
      {
        productId: 3,
        productName: '农机购置贷款',
        bankName: '中国工商银行',
        maxAmount: 300000,
        rate: 4.65,
        maxTerm: 24,
        description: '专门用于购买农机设备的贷款，助力农业机械化',
        conditions: '购买指定农机设备，提供购机合同'
      },
      {
        productId: 4,
        productName: '种植大户贷',
        bankName: '中国建设银行',
        maxAmount: 500000,
        rate: 4.55,
        maxTerm: 36,
        description: '面向种植大户的大额贷款，支持规模化农业生产',
        conditions: '种植面积达到100亩以上，有种植经验'
      },
      {
        productId: 5,
        productName: '农产品加工贷',
        bankName: '中国银行',
        maxAmount: 800000,
        rate: 4.85,
        maxTerm: 48,
        description: '支持农产品加工企业的流动资金贷款',
        conditions: '具有加工资质，有稳定的订单来源'
      },
      {
        productId: 6,
        productName: '农业设施贷',
        bankName: '中国邮政储蓄银行',
        maxAmount: 200000,
        rate: 4.45,
        maxTerm: 24,
        description: '用于建设温室大棚、仓储设施等农业基础设施',
        conditions: '有建设用地手续，项目可行性报告'
      }
    ]
  } catch (error) {
    ElMessage.error('加载产品列表失败')
  }
}

// 查看详情
const handleViewDetail = (product) => {
  currentProduct.value = product
  detailVisible.value = true
}

// 申请融资
const handleApply = (product) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    goToLogin()
    return
  }
  if (userStore.role !== 'farmer') {
    ElMessage.warning('只有农户可以申请融资')
    return
  }
  // 跳转到融资申请页面
  router.push('/farmer/finance')
  detailVisible.value = false
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.finance-products-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #4CAF50;
  padding: 0;
  height: 60px;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
}

.nav-menu {
  flex: 1;
  border: none;
  background: transparent;
}

.nav-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.8);
  border-bottom: 2px solid transparent;
}

.nav-menu .el-menu-item:hover,
.nav-menu .el-menu-item.is-active {
  color: #fff;
  background: transparent;
  border-bottom-color: #fff;
}

.header-actions {
  display: flex;
  align-items: center;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #fff;
  cursor: pointer;
}

.main {
  flex: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.page-title {
  margin-bottom: 30px;
  color: #333;
  text-align: center;
}

.finance-card {
  margin-bottom: 20px;
  height: 100%;
}

.card-header {
  display: flex;
  gap: 20px;
  align-items: center;
}

.card-header h3 {
  margin: 0 0 5px 0;
  color: #333;
}

.card-header .bank {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.card-body .info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.card-body .info-item span:first-child {
  color: #666;
}

.card-body .info-item .value {
  font-weight: bold;
  color: #333;
}

.card-footer {
  display: flex;
  gap: 10px;
}

.product-detail h2 {
  margin-bottom: 20px;
  color: #333;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}
</style>
