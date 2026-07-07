<template>
  <div class="market-page">
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
      <el-card>
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <el-radio-group v-model="filterType" @change="loadProducts">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="source">货源</el-radio-button>
            <el-radio-button value="demand">需求</el-radio-button>
          </el-radio-group>
          <el-select v-model="categoryFilter" placeholder="选择类别" clearable @change="loadProducts">
            <el-option label="粮食" value="粮食" />
            <el-option label="蔬菜" value="蔬菜" />
            <el-option label="水果" value="水果" />
            <el-option label="畜禽" value="畜禽" />
            <el-option label="水产" value="水产" />
            <el-option label="其他" value="其他" />
          </el-select>
          <el-input v-model="keyword" placeholder="搜索商品" clearable @keyup.enter="loadProducts">
            <template #append>
              <el-button @click="loadProducts">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>

        <!-- 商品列表 -->
        <el-row :gutter="20" class="product-list">
          <el-col :span="6" v-for="product in productList" :key="product.orderId">
            <div class="product-card" @click="handleViewDetail(product)">
              <div class="product-image">
                <img :src="product.imageUrl" :alt="product.productName" />
                <el-tag v-if="product.type === 'demand'" type="warning" class="type-tag">需求</el-tag>
              </div>
              <div class="product-info">
                <h4>{{ product.productName }}</h4>
                <p class="price">
                  <template v-if="product.type === 'source'">
                    ¥{{ product.price }}/{{ product.unit }}
                  </template>
                  <template v-else>
                    面议
                  </template>
                </p>
                <p class="location">{{ product.origin }}</p>
                <p class="stock">库存：{{ product.stock }}{{ product.unit }}</p>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadProducts"
            @current-change="loadProducts"
          />
        </div>
      </el-card>
    </el-main>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="商品详情" width="600px">
      <div v-if="currentProduct" class="product-detail">
        <img :src="currentProduct.imageUrl" :alt="currentProduct.productName" class="detail-image" />
        <h2>{{ currentProduct.productName }}</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="类别">{{ currentProduct.category }}</el-descriptions-item>
          <el-descriptions-item label="产地">{{ currentProduct.origin }}</el-descriptions-item>
          <el-descriptions-item label="价格" v-if="currentProduct.type === 'source'">
            ¥{{ currentProduct.price }}/{{ currentProduct.unit }}
          </el-descriptions-item>
          <el-descriptions-item label="库存">{{ currentProduct.stock }}{{ currentProduct.unit }}</el-descriptions-item>
          <el-descriptions-item label="发布人">{{ currentProduct.farmerName }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ currentProduct.createTime }}</el-descriptions-item>
          <el-descriptions-item label="商品描述" span="2">{{ currentProduct.description }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer v-if="userStore.role === 'farmer' && currentProduct?.type === 'source'">
        <el-button type="primary" @click="handleAddToCart">
          <el-icon><ShoppingCart /></el-icon>
          加入购物车
        </el-button>
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
const filterType = ref('')
const categoryFilter = ref('')
const keyword = ref('')
const productList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
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

// 加载商品列表
const loadProducts = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await productApi.getPage({
    //   page: currentPage.value,
    //   size: pageSize.value,
    //   type: filterType.value,
    //   category: categoryFilter.value,
    //   keyword: keyword.value
    // })

    // 模拟数据
    productList.value = [
      {
        orderId: 1,
        productName: '有机大米',
        category: '粮食',
        price: 5.8,
        unit: '斤',
        stock: 5000,
        origin: '黑龙江五常',
        type: 'source',
        farmerName: '张三农场',
        description: '优质有机大米，无农药化肥',
        imageUrl: 'https://via.placeholder.com/300x300?text=Rice',
        createTime: '2024-01-15 10:00:00'
      },
      {
        orderId: 2,
        productName: '新鲜蔬菜',
        category: '蔬菜',
        price: 3.5,
        unit: '斤',
        stock: 2000,
        origin: '山东寿光',
        type: 'source',
        farmerName: '李四合作社',
        description: '新鲜时令蔬菜',
        imageUrl: 'https://via.placeholder.com/300x300?text=Vegetable',
        createTime: '2024-01-14 14:30:00'
      },
      {
        orderId: 3,
        productName: '求购优质小麦',
        category: '粮食',
        origin: '河南郑州',
        type: 'demand',
        farmerName: '王五贸易',
        description: '求购优质小麦100吨',
        imageUrl: 'https://via.placeholder.com/300x300?text=Wheat',
        createTime: '2024-01-13 09:00:00'
      }
    ]
    total.value = 3
  } catch (error) {
    ElMessage.error('加载商品列表失败')
  }
}

// 查看详情
const handleViewDetail = (product) => {
  currentProduct.value = product
  detailVisible.value = true
}

// 加入购物车
const handleAddToCart = () => {
  ElMessage.success('已加入购物车')
  detailVisible.value = false
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.market-page {
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

.filter-bar {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-bar .el-input {
  width: 300px;
}

.product-list {
  margin-bottom: 20px;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  position: relative;
  height: 180px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.type-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.product-info {
  padding: 15px;
}

.product-info h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-info .price {
  margin: 0 0 8px 0;
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.product-info .location,
.product-info .stock {
  margin: 0 0 5px 0;
  color: #999;
  font-size: 12px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.product-detail {
  text-align: center;
}

.detail-image {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 20px;
}

.product-detail h2 {
  margin-bottom: 20px;
  color: #333;
}

:deep(.el-menu--horizontal) {
  border-bottom: none;
}
</style>
