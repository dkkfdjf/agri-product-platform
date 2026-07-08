<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <header class="top-header">
      <div class="header-container">
        <!-- Logo -->
        <div class="logo-section" @click="goTo('/home')">
          <el-icon :size="32" color="#4CAF50"><Grid /></el-icon>
          <span class="platform-name">农品通</span>
        </div>

        <!-- 主导航 -->
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item active">
            <span>首页</span>
          </router-link>
          <router-link to="/market" class="nav-item">
            <span>农产品</span>
          </router-link>
          <router-link to="/finance-products" class="nav-item">
            <span>融资</span>
          </router-link>
          <router-link to="/expert-help" class="nav-item">
            <span>专家</span>
          </router-link>
          <span class="nav-item">
            <span>关于我们</span>
          </span>
        </nav>

        <!-- 右侧登录区 -->
        <div class="auth-section">
          <template v-if="!userStore.isLoggedIn()">
            <el-button class="login-btn" @click="goTo('/login')">登录</el-button>
            <el-button class="register-btn" @click="goTo('/register')">注册</el-button>
          </template>
          <template v-else>
            <el-dropdown @command="handleUserCommand">
              <div class="user-dropdown">
                <el-icon><Avatar /></el-icon>
                <span>{{ getUserDisplayName() }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="getDashboardPath()">个人中心</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-wrapper">
      <!-- 轮播图区域 -->
      <section class="hero-banner">
        <el-carousel height="420px" :interval="5000" arrow="hover">
          <el-carousel-item v-for="banner in homeData.banners" :key="banner.bannerId">
            <div class="banner-slide" @click="handleBannerClick(banner)">
              <img :src="banner.pic_path" :alt="banner.title" />
              <div class="banner-overlay" v-if="banner.title">
                <div class="banner-content">
                  <h1>{{ banner.title }}</h1>
                  <p v-if="banner.desc">{{ banner.desc }}</p>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
        <div class="banner-placeholder" v-if="!homeData.banners || homeData.banners.length === 0">
          <el-carousel height="420px">
            <el-carousel-item>
              <div class="default-banner-slide">
                <div class="banner-content">
                  <h1>智慧农业 · 融销一体平台</h1>
                  <p>连接农户与市场，助力乡村振兴</p>
                </div>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
      </section>

      <!-- 功能入口 -->
      <section class="feature-entry">
        <div class="entry-container">
          <div class="entry-item" @click="goTo('/market')">
            <div class="entry-image">
              <img src="https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=400&h=300&fit=crop" alt="农产品交易" />
              <div class="entry-overlay">
                <div class="entry-content">
                  <h4>农产品交易</h4>
                  <p>货源发布 · 需求对接 · 在线交易</p>
                </div>
              </div>
            </div>
          </div>
          <div class="entry-item" @click="goTo('/finance-products')">
            <div class="entry-image">
              <img src="https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400&h=300&fit=crop" alt="融资服务" />
              <div class="entry-overlay">
                <div class="entry-content">
                  <h4>融资服务</h4>
                  <p>农业贷款 · 快速审批 · 低息融资</p>
                </div>
              </div>
            </div>
          </div>
          <div class="entry-item" @click="goTo('/expert-help')">
            <div class="entry-image">
              <img src="https://images.unsplash.com/photo-1581092921461-eab62e97a782?w=400&h=300&fit=crop" alt="专家咨询" />
              <div class="entry-overlay">
                <div class="entry-content">
                  <h4>专家咨询</h4>
                  <p>技术咨询 · 在线问答 · 专家预约</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 商品展示 -->
      <section class="content-section" v-if="homeData.products && homeData.products.length > 0">
        <div class="section-header">
          <h3 class="section-title">农产品供需</h3>
          <router-link to="/market" class="view-more">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="product-grid">
          <div
            class="product-card"
            v-for="product in homeData.products.slice(0, 8)"
            :key="product.order_id"
            @click="goToProduct(product)"
          >
            <div class="product-image">
              <img :src="product.picture || defaultProductImage" :alt="product.title" />
              <span class="product-badge" :class="product.type === 'goods' ? 'badge-supply' : 'badge-demand'">
                {{ product.type === 'goods' ? '货源' : '需求' }}
              </span>
            </div>
            <div class="product-info">
              <h4 class="product-name">{{ product.title }}</h4>
              <p class="product-desc">{{ product.content }}</p>
              <div class="product-footer">
                <div class="product-price" v-if="product.price">
                  <span class="price-symbol">¥</span>
                  <span class="price-amount">{{ product.price }}</span>
                </div>
                <span class="product-status" :class="'status-' + product.order_status">
                  {{ getStatusText(product.order_status) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 融资产品展示 -->
      <section class="content-section" v-if="homeData.financeProducts && homeData.financeProducts.length > 0">
        <div class="section-header">
          <h3 class="section-title">融资产品</h3>
          <router-link to="/finance-products" class="view-more">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="finance-grid">
          <div
            class="finance-card"
            v-for="finance in homeData.financeProducts.slice(0, 6)"
            :key="finance.product_id"
            @click="goTo('/finance-products')"
          >
            <div class="finance-icon" style="background: linear-gradient(135deg, #2196F3 0%, #42A5F5 100%)">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="finance-info">
              <h4 class="finance-name">{{ finance.bank_name }}</h4>
              <p class="finance-desc">{{ finance.introduce }}</p>
              <div class="finance-details">
                <span class="detail-item">
                  <em>可贷</em>
                  <strong>{{ formatMoney(finance.money) }}元</strong>
                </span>
                <span class="detail-item">
                  <em>利率</em>
                  <strong>{{ finance.rate }}%</strong>
                </span>
                <span class="detail-item">
                  <em>期限</em>
                  <strong>{{ finance.repayment }}个月</strong>
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 专家展示 -->
      <section class="content-section" v-if="homeData.experts && homeData.experts.length > 0">
        <div class="section-header">
          <h3 class="section-title">专家团队</h3>
          <router-link to="/expert-help" class="view-more">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="expert-grid">
          <div
            class="expert-card"
            v-for="expert in homeData.experts.slice(0, 6)"
            :key="expert.user_name"
            @click="goTo('/expert-help')"
          >
            <div class="expert-avatar">
              <el-icon :size="28"><UserFilled /></el-icon>
            </div>
            <div class="expert-info">
              <h4 class="expert-name">{{ expert.real_name }}</h4>
              <p class="expert-title">{{ expert.profession }}</p>
              <p class="expert-org" v-if="expert.belong">{{ expert.belong }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 知识展示 -->
      <section class="content-section" v-if="homeData.knowledges && homeData.knowledges.length > 0">
        <div class="section-header">
          <h3 class="section-title">农业知识</h3>
          <router-link to="/expert-help" class="view-more">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="knowledge-list">
          <div
            class="knowledge-item"
            v-for="knowledge in homeData.knowledges.slice(0, 4)"
            :key="knowledge.knowledge_id"
            @click="goTo('/expert-help')"
          >
            <div class="knowledge-thumb" v-if="knowledge.pic_path">
              <img :src="knowledge.pic_path" :alt="knowledge.title" />
            </div>
            <div class="knowledge-content">
              <h4 class="knowledge-title">{{ knowledge.title }}</h4>
              <p class="knowledge-excerpt">{{ knowledge.content }}</p>
              <div class="knowledge-meta">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ knowledge.own_name }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 问答展示 -->
      <section class="content-section" v-if="homeData.questions && homeData.questions.length > 0">
        <div class="section-header">
          <h3 class="section-title">热门问答</h3>
          <router-link to="/expert-help" class="view-more">
            查看更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="qa-list">
          <div
            class="qa-item"
            v-for="question in homeData.questions.slice(0, 4)"
            :key="question.id"
            @click="goTo('/expert-help')"
          >
            <div class="qa-badge" :class="question.status === 1 ? 'badge-answered' : 'badge-pending'">
              {{ question.status === 1 ? '已回答' : '待回答' }}
            </div>
            <h4 class="qa-title">{{ question.title }}</h4>
            <p class="qa-content">{{ question.question }}</p>
            <div class="qa-meta">
              <span class="meta-tag">
                <el-icon><Grid /></el-icon>
                {{ question.plant_name }}
              </span>
              <span class="meta-tag">
                <el-icon><User /></el-icon>
                {{ question.expert_name }}
              </span>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="site-footer">
      <div class="footer-top">
        <div class="footer-brand">
          <div class="footer-logo">
            <el-icon :size="28" color="#4CAF50"><Grid /></el-icon>
            <span>农品通</span>
          </div>
          <p class="footer-slogan">农产品融销一体平台</p>
          <p class="footer-desc">连接农户与市场，助力乡村振兴</p>
        </div>
        <div class="footer-links">
          <div class="link-column">
            <h4>平台服务</h4>
            <router-link to="/market">农产品交易</router-link>
            <router-link to="/finance-products">融资服务</router-link>
            <router-link to="/expert-help">专家助力</router-link>
          </div>
          <div class="link-column">
            <h4>关于我们</h4>
            <a>平台介绍</a>
            <a>联系我们</a>
            <a>服务条款</a>
          </div>
          <div class="link-column">
            <h4>帮助中心</h4>
            <a>用户指南</a>
            <a>常见问题</a>
            <a>隐私政策</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2024 农品通 - 农产品融销一体平台 All Rights Reserved</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Grid,
  User,
  Avatar,
  ArrowDown,
  ArrowRight,
  Money,
  UserFilled,
  ShoppingCart,
  Wallet,
  ChatDotRound
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()

// 首页数据
const homeData = ref({
  banners: [],
  products: [],
  financeProducts: [],
  experts: [],
  knowledges: [],
  questions: []
})

// 默认商品图片
const defaultProductImage = 'https://via.placeholder.com/200x200?text=农产品'

// 加载首页数据
const loadHomeData = async () => {
  try {
    const response = await axios.get('/api/home')
    if (response.data.code === 200) {
      homeData.value = response.data.data
    }
  } catch (error) {
    console.error('加载首页数据失败:', error)
    loadMockData()
  }
}

// 模拟数据
const loadMockData = () => {
  homeData.value = {
    banners: [
      {
        banner_id: 1,
        title: '智慧农业 · 融销一体平台',
        desc: '连接农户与市场，助力乡村振兴',
        pic_path: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=1200&h=420&fit=crop',
        link_url: '/market'
      },
      {
        banner_id: 2,
        title: '农业融资服务',
        desc: '低息贷款，快速审批，助力农业生产',
        pic_path: 'https://images.unsplash.com/photo-1574943320219-55edc8c51b26?w=1200&h=420&fit=crop',
        link_url: '/finance-products'
      },
      {
        banner_id: 3,
        title: '专家在线咨询',
        desc: '专业技术指导，解答农业生产难题',
        pic_path: 'https://images.unsplash.com/photo-1581092921461-eab62e97a782?w=1200&h=420&fit=crop',
        link_url: '/expert-help'
      }
    ],
    products: [
      {
        order_id: 1,
        title: '优质五常大米',
        content: '东北黑土地种植，口感香甜，营养丰富',
        price: 5.8,
        type: 'goods',
        order_status: 0,
        picture: 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=200&h=200&fit=crop'
      },
      {
        order_id: 2,
        title: '求购新鲜蔬菜',
        content: '长期采购各类时令蔬菜，量大从优',
        type: 'demand',
        order_status: 0,
        picture: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=200&h=200&fit=crop'
      },
      {
        order_id: 3,
        title: '山东红富士苹果',
        content: '脆甜多汁，产地直发，品质保证',
        price: 8.0,
        type: 'goods',
        order_status: 1,
        picture: 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=200&h=200&fit=crop'
      },
      {
        order_id: 4,
        title: '散养土鸡蛋',
        content: '农家散养，无添加剂，营养丰富',
        price: 1.5,
        type: 'goods',
        order_status: 0,
        picture: 'https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=200&h=200&fit=crop'
      },
      {
        order_id: 5,
        title: '有机蔬菜',
        content: '绿色种植，健康安全',
        price: 6.0,
        type: 'goods',
        order_status: 0,
        picture: 'https://images.unsplash.com/photo-1518459031867-a89b944bffe4?w=200&h=200&fit=crop'
      },
      {
        order_id: 6,
        title: '新鲜水果',
        content: '时令水果，新鲜直达',
        price: 12.0,
        type: 'goods',
        order_status: 0,
        picture: 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=200&h=200&fit=crop'
      },
      {
        order_id: 7,
        title: '求购大宗粮食',
        content: '求购小麦、玉米等大宗粮食',
        type: 'demand',
        order_status: 0,
        picture: 'https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=200&h=200&fit=crop'
      },
      {
        order_id: 8,
        title: '绿色有机大米',
        content: '无公害种植，品质保证',
        price: 7.5,
        type: 'goods',
        order_status: 0,
        picture: 'https://images.unsplash.com/photo-1536304929831-ee1ca9d44906?w=200&h=200&fit=crop'
      }
    ],
    financeProducts: [
      {
        product_id: 1,
        bank_name: '农业银行',
        introduce: '惠农贷，专门为农户设计的低息贷款产品',
        bank_phone: '400-123-4567',
        money: 500000,
        rate: 4.35,
        repayment: 24
      },
      {
        product_id: 2,
        bank_name: '农村信用社',
        introduce: '种植贷，支持农业生产全过程',
        bank_phone: '400-234-5678',
        money: 300000,
        rate: 4.5,
        repayment: 18
      },
      {
        product_id: 3,
        bank_name: '工商银行',
        introduce: '农企贷，助力农业企业做大做强',
        bank_phone: '400-345-6789',
        money: 1000000,
        rate: 4.2,
        repayment: 36
      }
    ],
    experts: [
      {
        user_name: 'expert1',
        real_name: '张教授',
        profession: '作物栽培学',
        belong: '中国农业大学'
      },
      {
        user_name: 'expert2',
        real_name: '李专家',
        profession: '植物保护',
        belong: '农业科学院'
      },
      {
        user_name: 'expert3',
        real_name: '王研究员',
        profession: '土壤肥料',
        belong: '农业技术推广站'
      },
      {
        user_name: 'expert4',
        real_name: '赵高级农艺师',
        profession: '果树栽培',
        belong: '果树研究所'
      },
      {
        user_name: 'expert5',
        real_name: '刘专家',
        profession: '畜牧兽医',
        belong: '畜牧科学院'
      },
      {
        user_name: 'expert6',
        real_name: '陈研究员',
        profession: '农业机械化',
        belong: '农机研究所'
      }
    ],
    knowledges: [
      {
        knowledge_id: 1,
        title: '水稻种植技术要点',
        content: '详细介绍水稻从选种到收获的全过程技术要点...',
        own_name: '张教授',
        pic_path: 'https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=100&h=100&fit=crop'
      },
      {
        knowledge_id: 2,
        title: '果树病虫害防治',
        content: '春季果树病虫害预防措施...',
        own_name: '李专家'
      },
      {
        knowledge_id: 3,
        title: '有机蔬菜种植指南',
        content: '如何种植绿色有机蔬菜...',
        own_name: '王研究员'
      },
      {
        knowledge_id: 4,
        title: '土壤改良技术',
        content: '提升土壤肥力的有效方法...',
        own_name: '王研究员'
      }
    ],
    questions: [
      {
        id: 1,
        title: '水稻叶子发黄怎么办？',
        question: '我家水稻最近叶子开始发黄，不知道是什么原因...',
        plant_name: '水稻',
        expert_name: '张教授',
        status: 1
      },
      {
        id: 2,
        title: '苹果树什么时候修剪？',
        question: '请问苹果树应该在什么季节进行修剪比较好？',
        plant_name: '苹果',
        expert_name: '赵高级农艺师',
        status: 0
      },
      {
        id: 3,
        title: '蔬菜病虫害防治方法',
        question: '大棚蔬菜经常有病虫害，有什么好的防治方法吗？',
        plant_name: '蔬菜',
        expert_name: '李专家',
        status: 1
      },
      {
        id: 4,
        title: '土壤酸化怎么处理',
        question: '检测发现土壤偏酸，应该怎么改良？',
        plant_name: '通用',
        expert_name: '王研究员',
        status: 0
      }
    ]
  }
}

// 格式化金额
const formatMoney = (money) => {
  return new Intl.NumberFormat('zh-CN').format(money)
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待交易',
    1: '交易中',
    2: '已完成'
  }
  return statusMap[status] || '未知'
}

// 获取用户显示名称
const getUserDisplayName = () => {
  const userInfo = userStore.userInfo
  if (!userInfo) return ''

  const role = userStore.role
  switch (role) {
    case 'farmer':
      return userInfo.real_name || userInfo.user_name
    case 'expert':
      return userInfo.real_name || userInfo.user_name
    case 'bank':
      return userInfo.bank_name || userInfo.user_name
    case 'admin':
      return '管理员'
    case 'buyer':
      return userInfo.real_name || userInfo.user_name
    default:
      return userInfo.user_name
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
    case 'buyer':
      return '/buyer/dashboard'
    default:
      return '/login'
  }
}

// 处理用户下拉菜单命令
const handleUserCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    location.reload()
  } else {
    goTo(command)
  }
}

// 页面跳转
const goTo = (path) => {
  router.push(path)
}

// 跳转到商品详情
const goToProduct = (product) => {
  goTo('/market')
}

// 轮播图点击
const handleBannerClick = (banner) => {
  if (banner.link_url) {
    goTo(banner.link_url)
  }
}

onMounted(() => {
  loadHomeData()
})
</script>

<style scoped>
/* 全局样式 */
.home-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* 顶部导航栏 */
.top-header {
  background: #fff;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s;
}

.logo-section:hover {
  transform: scale(1.02);
}

.platform-name {
  font-size: 22px;
  font-weight: 700;
  color: #4CAF50;
  letter-spacing: 0.5px;
}

.nav-menu {
  display: flex;
  gap: 8px;
}

.nav-item {
  padding: 8px 20px;
  color: #333;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.25s ease;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  position: relative;
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: #4CAF50;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.nav-item:hover::after,
.nav-item.active::after {
  width: calc(100% - 40px);
}

.nav-item:hover,
.nav-item.active {
  color: #4CAF50;
  background: rgba(76, 175, 80, 0.08);
}

.auth-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.login-btn {
  background: transparent !important;
  color: #4CAF50 !important;
  font-weight: 500;
  padding: 8px 24px !important;
  border: 1px solid #4CAF50 !important;
  border-radius: 8px !important;
}

.login-btn:hover {
  background: rgba(76, 175, 80, 0.1) !important;
}

.register-btn {
  padding: 8px 24px !important;
  font-weight: 500;
  background: #4CAF50 !important;
  color: #fff !important;
  border-color: #4CAF50 !important;
  border-radius: 8px !important;
}

.register-btn:hover {
  background: #43a047 !important;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(76, 175, 80, 0.08);
  transition: all 0.25s ease;
  font-size: 14px;
  font-weight: 500;
}

.user-dropdown:hover {
  background: rgba(76, 175, 80, 0.15);
}

/* 主内容区 */
.main-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

/* 轮播图区域 */
.hero-banner {
  margin-bottom: 32px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}

.banner-slide {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.banner-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 50px 40px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.5));
}

.banner-content {
  max-width: 800px;
  color: #fff;
}

.banner-content h1 {
  font-size: 38px;
  margin-bottom: 10px;
  font-weight: 600;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.25);
}

.banner-content p {
  font-size: 16px;
  opacity: 0.95;
}

.default-banner-slide {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #4CAF50 0%, #43a047 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 功能入口 */
.feature-entry {
  margin-bottom: 40px;
}

.entry-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.entry-item {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.04);
  background: #fff;
}

.entry-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  border-color: rgba(76, 175, 80, 0.15);
}

.entry-image {
  position: relative;
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.entry-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.entry-item:hover .entry-image img {
  transform: scale(1.1);
}

.entry-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 20px 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  transition: all 0.3s ease;
}

.entry-content {
  color: #fff;
  text-align: center;
}

.entry-content h4 {
  font-size: 20px;
  color: #fff;
  margin-bottom: 8px;
  font-weight: 600;
}

.entry-content p {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.5;
}

/* 内容区块 */
.content-section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 20px;
  color: #333;
  margin: 0;
  font-weight: 600;
}

.view-more {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #4CAF50;
  text-decoration: none;
  font-size: 14px;
  transition: gap 0.2s ease;
}

.view-more:hover {
  gap: 8px;
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.product-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: rgba(76, 175, 80, 0.1);
}

.product-image {
  position: relative;
  width: 100%;
  height: 180px;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: #fff;
}

.badge-supply {
  background: linear-gradient(135deg, #4CAF50 0%, #66BB6A 100%);
}

.badge-demand {
  background: linear-gradient(135deg, #FF9800 0%, #FFB74D 100%);
}

.product-info {
  padding: 14px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  display: flex;
  align-items: baseline;
  color: #f5222d;
}

.price-symbol {
  font-size: 12px;
}

.price-amount {
  font-size: 18px;
  font-weight: 600;
}

.product-status {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
}

.status-0 {
  background: #e8f5e9;
  color: #4CAF50;
}

.status-1 {
  background: #fff3e0;
  color: #FF9800;
}

.status-2 {
  background: #e0e0e0;
  color: #666;
}

/* 融资产品网格 */
.finance-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.finance-card {
  display: flex;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.04);
  gap: 16px;
}

.finance-card:hover {
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.1);
  transform: translateY(-2px);
  border-color: rgba(33, 150, 243, 0.1);
}

.finance-icon {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.finance-info {
  flex: 1;
}

.finance-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.finance-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.finance-details {
  display: flex;
  gap: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-item em {
  font-size: 10px;
  color: #999;
  margin-bottom: 2px;
  font-style: normal;
}

.detail-item strong {
  font-size: 13px;
  color: #333;
  font-weight: 600;
}

/* 专家网格 */
.expert-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}

.expert-card {
  text-align: center;
  padding: 20px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.expert-card:hover {
  box-shadow: 0 4px 20px rgba(255, 152, 0, 0.1);
  transform: translateY(-2px);
  border-color: rgba(255, 152, 0, 0.1);
}

.expert-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF9800 0%, #FFB74D 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin: 0 auto 12px;
}

.expert-name {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.expert-title {
  font-size: 11px;
  color: #4CAF50;
  margin-bottom: 4px;
}

.expert-org {
  font-size: 10px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 知识列表 */
.knowledge-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.knowledge-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.knowledge-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: rgba(76, 175, 80, 0.1);
}

.knowledge-thumb {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.knowledge-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.knowledge-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.knowledge-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.knowledge-excerpt {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.knowledge-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

/* 问答列表 */
.qa-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.qa-item {
  padding: 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 1px 8px rgba(0, 0, 0, 0.04);
  background: #fff;
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.qa-item:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: rgba(76, 175, 80, 0.1);
}

.qa-badge {
  display: inline-block;
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 11px;
  margin-bottom: 10px;
}

.badge-answered {
  background: #e8f5e9;
  color: #4CAF50;
}

.badge-pending {
  background: #fff3e0;
  color: #FF9800;
}

.qa-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.qa-content {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.qa-meta {
  display: flex;
  gap: 12px;
}

.meta-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #999;
}

/* 页脚 */
.site-footer {
  background: #1a1a1a;
  color: rgba(255, 255, 255, 0.7);
  margin-top: 60px;
}

.footer-top {
  max-width: 1400px;
  margin: 0 auto;
  padding: 48px 24px;
  display: flex;
  justify-content: space-between;
}

.footer-brand {
  flex: 1;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  color: #fff;
  font-size: 18px;
  font-weight: 500;
}

.footer-slogan {
  font-size: 14px;
  color: #fff;
  margin-bottom: 8px;
}

.footer-desc {
  font-size: 13px;
  opacity: 0.7;
}

.footer-links {
  display: flex;
  gap: 60px;
}

.link-column h4 {
  color: #fff;
  margin-bottom: 16px;
  font-size: 14px;
}

.link-column a {
  display: block;
  margin-bottom: 10px;
  font-size: 13px;
  color: inherit;
  text-decoration: none;
  opacity: 0.7;
  transition: opacity 0.3s;
}

.link-column a:hover {
  opacity: 1;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding: 20px;
  text-align: center;
  font-size: 13px;
  opacity: 0.6;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .expert-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }

  .header-container {
    padding: 0 16px;
  }

  .main-wrapper {
    padding: 16px;
  }

  .entry-container {
    grid-template-columns: 1fr;
  }

  .product-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .finance-grid {
    grid-template-columns: 1fr;
  }

  .expert-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .knowledge-list,
  .qa-list {
    grid-template-columns: 1fr;
  }

  .footer-top {
    flex-direction: column;
    gap: 32px;
  }

  .footer-links {
    flex-wrap: wrap;
    gap: 32px;
  }
}

@media (max-width: 480px) {
  .product-grid {
    grid-template-columns: 1fr;
  }

  .expert-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
