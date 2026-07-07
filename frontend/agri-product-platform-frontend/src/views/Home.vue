<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <div class="top-bar">
      <div class="top-bar-content">
        <span class="slogan">农产品融销一体平台</span>
        <div class="top-bar-right">
          <template v-if="!userStore.isLoggedIn()">
            <span class="link" @click="goToLogin">登录</span>
            <span class="link" @click="goToLogin">注册</span>
          </template>
          <template v-else>
            <span class="link" @click="goTo(getDashboardPath())">{{ getUserDisplayName() }}</span>
            <span class="link" @click="handleLogout">退出</span>
          </template>
        </div>
      </div>
    </div>

    <!-- 主导航栏 -->
    <div class="main-nav">
      <div class="nav-content">
        <div class="logo-area">
          <el-icon :size="36" color="#4CAF50"><Grid /></el-icon>
          <span class="logo-text">农品通</span>
        </div>
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索农产品、融资产品、专家"
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #suffix>
              <el-icon @click="handleSearch" style="cursor: pointer"><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="nav-links">
          <span class="nav-link" @click="goTo('/')">首页</span>
          <span class="nav-link" @click="goTo('/market')">农产品</span>
          <span class="nav-link" @click="goTo('/finance-products')">融资</span>
          <span class="nav-link" @click="goTo('/expert-help')">专家</span>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-wrapper">
      <!-- 轮播图 -->
      <div class="carousel-section">
        <el-carousel height="200px" indicator-position="outside">
          <el-carousel-item v-for="banner in bannerList" :key="banner.bannerId">
            <div class="carousel-item" @click="handleBannerClick(banner)">
              <img :src="banner.imageUrl" :alt="banner.title" />
              <div class="carousel-text">
                <h3>{{ banner.title }}</h3>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 分类入口 -->
      <div class="category-grid">
        <div class="category-item" @click="goTo('/market?type=粮食')">
          <div class="category-icon" style="background: linear-gradient(135deg, #ffd89b 0%, #19547b 100%)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <span>粮食</span>
        </div>
        <div class="category-item" @click="goTo('/market?type=蔬菜')">
          <div class="category-icon" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <span>蔬菜</span>
        </div>
        <div class="category-item" @click="goTo('/market?type=水果')">
          <div class="category-icon" style="background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <span>水果</span>
        </div>
        <div class="category-item" @click="goTo('/market?type=畜禽')">
          <div class="category-icon" style="background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <span>畜禽</span>
        </div>
        <div class="category-item" @click="goTo('/market?type=水产')">
          <div class="category-icon" style="background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)">
            <el-icon :size="24"><TrendCharts /></el-icon>
          </div>
          <span>水产</span>
        </div>
        <div class="category-item" @click="goTo('/finance-products')">
          <div class="category-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon :size="24"><Wallet /></el-icon>
          </div>
          <span>融资</span>
        </div>
        <div class="category-item" @click="goTo('/expert-help')">
          <div class="category-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon :size="24"><User /></el-icon>
          </div>
          <span>专家</span>
        </div>
        <div class="category-item" @click="goTo('/market')">
          <div class="category-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon :size="24"><More /></el-icon>
          </div>
          <span>更多</span>
        </div>
      </div>

      <!-- 商品列表区域 -->
      <div class="product-section">
        <!-- Tab切换 -->
        <el-tabs v-model="activeTab" class="product-tabs">
          <el-tab-pane label="推荐" name="recommend">
            <div class="product-grid">
              <div class="product-card" v-for="product in recommendProducts" :key="product.orderId" @click="goTo('/market')">
                <div class="product-img-wrapper">
                  <img :src="product.imageUrl" :alt="product.productName" />
                  <div class="product-tag" v-if="product.tag">{{ product.tag }}</div>
                </div>
                <div class="product-info">
                  <h4 class="product-name">{{ product.productName }}</h4>
                  <p class="product-desc">{{ product.description }}</p>
                  <div class="product-price-row">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="unit">/{{ product.unit }}</span>
                  </div>
                  <div class="product-footer">
                    <span class="sales">{{ product.sales }}人付款</span>
                    <el-button type="success" size="small" @click.stop="handleBuy(product)">
                      立即购买
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="粮食" name="grain">
            <div class="product-grid">
              <div class="product-card" v-for="product in grainProducts" :key="product.orderId">
                <div class="product-img-wrapper">
                  <img :src="product.imageUrl" :alt="product.productName" />
                </div>
                <div class="product-info">
                  <h4 class="product-name">{{ product.productName }}</h4>
                  <p class="product-desc">{{ product.description }}</p>
                  <div class="product-price-row">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="unit">/{{ product.unit }}</span>
                  </div>
                  <div class="product-footer">
                    <span class="sales">{{ product.sales }}人付款</span>
                    <el-button type="success" size="small" @click.stop="handleBuy(product)">
                      立即购买
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="蔬菜" name="vegetable">
            <div class="product-grid">
              <div class="product-card" v-for="product in vegetableProducts" :key="product.orderId">
                <div class="product-img-wrapper">
                  <img :src="product.imageUrl" :alt="product.productName" />
                </div>
                <div class="product-info">
                  <h4 class="product-name">{{ product.productName }}</h4>
                  <p class="product-desc">{{ product.description }}</p>
                  <div class="product-price-row">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="unit">/{{ product.unit }}</span>
                  </div>
                  <div class="product-footer">
                    <span class="sales">{{ product.sales }}人付款</span>
                    <el-button type="success" size="small" @click.stop="handleBuy(product)">
                      立即购买
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="水果" name="fruit">
            <div class="product-grid">
              <div class="product-card" v-for="product in fruitProducts" :key="product.orderId">
                <div class="product-img-wrapper">
                  <img :src="product.imageUrl" :alt="product.productName" />
                </div>
                <div class="product-info">
                  <h4 class="product-name">{{ product.productName }}</h4>
                  <p class="product-desc">{{ product.description }}</p>
                  <div class="product-price-row">
                    <span class="price">¥{{ product.price }}</span>
                    <span class="unit">/{{ product.unit }}</span>
                  </div>
                  <div class="product-footer">
                    <span class="sales">{{ product.sales }}人付款</span>
                    <el-button type="success" size="small" @click.stop="handleBuy(product)">
                      立即购买
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 底部导航（移动端风格） -->
    <div class="bottom-nav">
      <div class="nav-item" @click="goTo('/')">
        <el-icon :size="20"><HomeFilled /></el-icon>
        <span>首页</span>
      </div>
      <div class="nav-item" @click="goTo('/market')">
        <el-icon :size="20"><Goods /></el-icon>
        <span>商品</span>
      </div>
      <div class="nav-item" @click="goTo('/finance-products')">
        <el-icon :size="20"><Wallet /></el-icon>
        <span>融资</span>
      </div>
      <div class="nav-item" @click="goTo(getDashboardPath())">
        <el-icon :size="20"><User /></el-icon>
        <span>我的</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const activeTab = ref('recommend')

// 轮播图列表
const bannerList = ref([])

// 各分类商品
const recommendProducts = ref([])
const grainProducts = ref([])
const vegetableProducts = ref([])
const fruitProducts = ref([])

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
      return '/login'
  }
}

// 处理退出登录
const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  location.reload()
}

// 跳转登录
const goToLogin = () => {
  router.push('/login')
}

// 页面跳转
const goTo = (path) => {
  router.push(path)
}

// 搜索
const handleSearch = () => {
  if (searchKeyword.value) {
    router.push(`/market?keyword=${searchKeyword.value}`)
  }
}

// 轮播图点击
const handleBannerClick = (banner) => {
  if (banner.link) {
    goTo(banner.link)
  }
}

// 购买商品
const handleBuy = (product) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    goToLogin()
    return
  }
  ElMessage.success('已加入购物车')
}

// 加载首页数据
const loadHomeData = async () => {
  try {
    // 模拟数据
    bannerList.value = [
      {
        bannerId: 1,
        title: '优质农产品直供',
        imageUrl: 'https://via.placeholder.com/800x200/4CAF50/ffffff?text=优质农产品',
        link: '/market'
      },
      {
        bannerId: 2,
        title: '农业融资服务',
        imageUrl: 'https://via.placeholder.com/800x200/FF9800/ffffff?text=农业融资',
        link: '/finance-products'
      },
      {
        bannerId: 3,
        title: '专家在线指导',
        imageUrl: 'https://via.placeholder.com/800x200/2196F3/ffffff?text=专家指导',
        link: '/expert-help'
      }
    ]

    // 推荐商品
    recommendProducts.value = [
      {
        orderId: 1,
        productName: '五常有机大米',
        description: '东北黑土地种植，口感香甜',
        price: 5.8,
        unit: '斤',
        sales: 128,
        tag: '热销',
        imageUrl: 'https://via.placeholder.com/200x200?text=大米'
      },
      {
        orderId: 2,
        productName: '山东寿光蔬菜',
        description: '新鲜时令蔬菜，产地直发',
        price: 3.5,
        unit: '斤',
        sales: 256,
        tag: '包邮',
        imageUrl: 'https://via.placeholder.com/200x200?text=蔬菜'
      },
      {
        orderId: 3,
        productName: '陕西洛川苹果',
        description: '脆甜多汁，营养丰富',
        price: 8.0,
        unit: '斤',
        sales: 89,
        tag: '新品',
        imageUrl: 'https://via.placeholder.com/200x200?text=苹果'
      },
      {
        orderId: 4,
        productName: '散养土鸡蛋',
        description: '农家散养，营养丰富',
        price: 1.5,
        unit: '个',
        sales: 512,
        tag: '',
        imageUrl: 'https://via.placeholder.com/200x200?text=鸡蛋'
      },
      {
        orderId: 5,
        productName: '农家散养土鸡',
        description: '山林散养，肉质鲜美',
        price: 128,
        unit: '只',
        sales: 67,
        tag: '优选',
        imageUrl: 'https://via.placeholder.com/200x200?text=土鸡'
      },
      {
        orderId: 6,
        productName: '新鲜草莓',
        description: '大棚种植，香甜可口',
        price: 15,
        unit: '斤',
        sales: 234,
        tag: '',
        imageUrl: 'https://via.placeholder.com/200x200?text=草莓'
      }
    ]

    // 粮食类
    grainProducts.value = [
      {
        orderId: 7,
        productName: '五常有机大米',
        description: '东北黑土地种植',
        price: 5.8,
        unit: '斤',
        sales: 128,
        imageUrl: 'https://via.placeholder.com/200x200?text=大米'
      },
      {
        orderId: 8,
        productName: '东北优质小米',
        description: '金黄饱满，营养健康',
        price: 8.5,
        unit: '斤',
        sales: 95,
        imageUrl: 'https://via.placeholder.com/200x200?text=小米'
      },
      {
        orderId: 9,
        productName: '内蒙古燕麦',
        description: '高寒地区种植',
        price: 6.8,
        unit: '斤',
        sales: 76,
        imageUrl: 'https://via.placeholder.com/200x200?text=燕麦'
      },
      {
        orderId: 10,
        productName: '东北大豆',
        description: '非转基因，优质蛋白',
        price: 3.5,
        unit: '斤',
        sales: 143,
        imageUrl: 'https://via.placeholder.com/200x200?text=大豆'
      }
    ]

    // 蔬菜类
    vegetableProducts.value = [
      {
        orderId: 11,
        productName: '山东寿光蔬菜',
        description: '新鲜时令蔬菜',
        price: 3.5,
        unit: '斤',
        sales: 256,
        imageUrl: 'https://via.placeholder.com/200x200?text=蔬菜'
      },
      {
        orderId: 12,
        productName: '有机西红柿',
        description: '自然成熟，酸甜可口',
        price: 6.0,
        unit: '斤',
        sales: 187,
        imageUrl: 'https://via.placeholder.com/200x200?text=西红柿'
      },
      {
        orderId: 13,
        productName: '新鲜黄瓜',
        description: '清脆爽口，顶花带刺',
        price: 4.5,
        unit: '斤',
        sales: 198,
        imageUrl: 'https://via.placeholder.com/200x200?text=黄瓜'
      },
      {
        orderId: 14,
        productName: '有机青菜',
        description: '无农药残留，健康安全',
        price: 3.0,
        unit: '斤',
        sales: 312,
        imageUrl: 'https://via.placeholder.com/200x200?text=青菜'
      }
    ]

    // 水果类
    fruitProducts.value = [
      {
        orderId: 15,
        productName: '陕西洛川苹果',
        description: '脆甜多汁',
        price: 8.0,
        unit: '斤',
        sales: 189,
        imageUrl: 'https://via.placeholder.com/200x200?text=苹果'
      },
      {
        orderId: 16,
        productName: '新疆哈密瓜',
        description: '香甜可口，来自西域',
        price: 12,
        unit: '斤',
        sales: 145,
        imageUrl: 'https://via.placeholder.com/200x200?text=哈密瓜'
      },
      {
        orderId: 17,
        productName: '新鲜草莓',
        description: '香甜可口，营养丰富',
        price: 15,
        unit: '斤',
        sales: 234,
        imageUrl: 'https://via.placeholder.com/200x200?text=草莓'
      },
      {
        orderId: 18,
        productName: '海南金钻凤梨',
        description: '热带水果，香甜多汁',
        price: 18,
        unit: '个',
        sales: 167,
        imageUrl: 'https://via.placeholder.com/200x200?text=凤梨'
      }
    ]
  } catch (error) {
    console.error('加载首页数据失败:', error)
  }
}

onMounted(() => {
  loadHomeData()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

/* 顶部条 */
.top-bar {
  background-color: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
  padding: 8px 0;
  font-size: 12px;
}

.top-bar-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px;
}

.top-bar .slogan {
  color: #999;
}

.top-bar-right {
  display: flex;
  gap: 20px;
}

.top-bar-right .link {
  color: #666;
  cursor: pointer;
}

.top-bar-right .link:hover {
  color: #4CAF50;
}

/* 主导航栏 */
.main-nav {
  background-color: #4CAF50;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 12px 15px;
  gap: 20px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.search-bar {
  flex: 1;
  max-width: 500px;
}

.search-input {
  border-radius: 20px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

.nav-links {
  display: flex;
  gap: 25px;
  color: rgba(255, 255, 255, 0.9);
}

.nav-link {
  cursor: pointer;
  transition: color 0.2s;
  white-space: nowrap;
}

.nav-link:hover {
  color: #fff;
}

/* 内容区域 */
.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px;
}

/* 轮播图 */
.carousel-section {
  margin-bottom: 15px;
}

.carousel-item {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
}

.carousel-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-text {
  position: absolute;
  bottom: 20px;
  left: 20px;
  color: #fff;
}

.carousel-text h3 {
  margin: 0;
  font-size: 24px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 分类网格 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  margin-bottom: 20px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s;
}

.category-item:hover {
  transform: scale(1.05);
}

.category-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 8px;
}

.category-item span {
  font-size: 13px;
  color: #333;
}

/* 商品区域 */
.product-section {
  background: #fff;
  border-radius: 8px;
  padding: 15px;
}

.product-tabs {
  margin-top: 10px;
}

:deep(.el-tabs__header) {
  margin-bottom: 15px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
}

:deep(.el-tabs__item.is-active) {
  color: #4CAF50;
}

:deep(.el-tabs__active-bar) {
  background-color: #4CAF50;
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

@media (min-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.product-card {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-img-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
}

.product-img-wrapper img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.product-info {
  padding: 12px;
}

.product-name {
  margin: 0 0 6px 0;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  height: 40px;
}

.product-desc {
  margin: 0 0 8px 0;
  font-size: 12px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.price {
  color: #ff4400;
  font-size: 18px;
  font-weight: bold;
}

.unit {
  color: #999;
  font-size: 12px;
  margin-left: 2px;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sales {
  font-size: 12px;
  color: #999;
}

/* 底部导航 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-around;
  padding: 8px 0;
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  color: #666;
  transition: color 0.2s;
  min-width: 60px;
}

.nav-item:hover {
  color: #4CAF50;
}

.nav-item span {
  font-size: 12px;
  margin-top: 4px;
}

@media (min-width: 768px) {
  .bottom-nav {
    display: none;
  }
}

@media (max-width: 767px) {
  .nav-links {
    display: none;
  }

  .search-bar {
    max-width: 200px;
  }

  .category-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>
