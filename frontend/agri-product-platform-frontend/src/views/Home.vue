<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <header class="home-header">
      <div class="header-inner">
        <div class="logo" @click="goTo('/')">
          <div class="logo-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M12 2L12 12M12 12L8 8M12 12L16 8M12 12L12 22M12 22L8 18M12 22L16 18" />
              <circle cx="12" cy="3" r="1.5" fill="currentColor"/>
            </svg>
          </div>
          <span class="logo-text">农融汇</span>
        </div>

        <nav class="main-nav">
          <a href="#products" @click.prevent="scrollToSection('products')">农产品</a>
          <a href="#financing" @click.prevent="scrollToSection('financing')">融资服务</a>
          <a href="#experts" @click.prevent="scrollToSection('experts')">专家助力</a>
          <a href="#knowledge" @click.prevent="scrollToSection('knowledge')">农业知识</a>
        </nav>

        <div class="header-actions">
          <template v-if="!userStore.isLoggedIn()">
            <button class="btn-text" @click="goTo('/login')">登录</button>
            <button class="btn-primary" @click="goTo('/register')">免费注册</button>
          </template>
          <template v-else>
            <el-dropdown @command="handleUserCommand" trigger="click">
              <div class="user-chip">
                <span class="user-name">{{ getUserDisplayName() }}</span>
                <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                  <path d="M4 6l4 4 4-4"/>
                </svg>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="getDashboardPath()">
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-bg">
        <div class="hero-pattern"></div>
      </div>
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            <span class="title-line">连接农户与市场</span>
            <span class="title-line title-accent">助力农业发展</span>
          </h1>
          <p class="hero-subtitle">
            融销一体的农产品服务平台 — 源源直达，融资无忧，专家伴您丰收
          </p>
          <div class="hero-actions">
            <button class="btn-hero btn-hero-primary" @click="goTo('/register')">
              <span>立即开始</span>
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 10h10M10 5l5 5-5 5"/>
              </svg>
            </button>
            <button class="btn-hero btn-hero-secondary" @click="scrollToSection('products')">
              浏览产品
            </button>
          </div>
        </div>
        <div class="hero-visual">
          <div class="visual-card visual-card-1">
            <div class="card-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 2L12 12M12 12L8 8M12 12L16 8M12 12L12 22M12 22L8 18M12 22L16 18" />
                <circle cx="12" cy="3" r="1.5" fill="currentColor"/>
                <ellipse cx="12" cy="18" rx="3" ry="5" fill="currentColor" opacity="0.3"/>
              </svg>
            </div>
            <div class="card-label">农产品交易</div>
          </div>
          <div class="visual-card visual-card-2">
            <div class="card-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
                <circle cx="12" cy="12" r="2" fill="currentColor"/>
                <path d="M8 8l2-2M8 16l2 2M16 8l-2-2M16 16l-2 2" stroke-width="1"/>
              </svg>
            </div>
            <div class="card-label">融资服务</div>
          </div>
          <div class="visual-card visual-card-3">
            <div class="card-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <circle cx="12" cy="8" r="3"/>
                <path d="M12 11v4M12 15l-3 3M12 15l3 3"/>
                <path d="M8 21h8a2 2 0 002-2v-5a2 2 0 00-2-2H8a2 2 0 00-2 2v5a2 2 0 002 2z"/>
                <path d="M9 6l1.5-1.5M15 6l-1.5-1.5" stroke-width="1"/>
              </svg>
            </div>
            <div class="card-label">专家咨询</div>
          </div>
        </div>
      </div>

      <!-- Trust Indicators -->
      <div class="trust-bar">
        <div class="trust-item">
          <div class="trust-value">10,000+</div>
          <div class="trust-label">注册农户</div>
        </div>
        <div class="trust-divider"></div>
        <div class="trust-item">
          <div class="trust-value">¥5亿+</div>
          <div class="trust-label">融资总额</div>
        </div>
        <div class="trust-divider"></div>
        <div class="trust-item">
          <div class="trust-value">500+</div>
          <div class="trust-label">合作专家</div>
        </div>
        <div class="trust-divider"></div>
        <div class="trust-item">
          <div class="trust-value">98%</div>
          <div class="trust-label">用户满意度</div>
        </div>
      </div>
    </section>

    <!-- Products Section -->
    <section id="products" class="products-section">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-eyebrow">优质货源</div>
          <h2 class="section-title">精选农产品</h2>
          <p class="section-subtitle">直接从农户到您的手中，新鲜、优质、价格透明</p>
        </div>

        <div class="products-grid">
          <div v-for="product in featuredProducts" :key="product.id"
               class="product-card"
               @click="viewProductDetail(product)">
            <div class="product-image">
              <img :src="product.image" :alt="product.name" />
              <span v-if="product.badge" class="product-badge">{{ product.badge }}</span>
            </div>
            <div class="product-content">
              <div class="product-category">{{ product.category }}</div>
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-origin">{{ product.origin }}</p>
              <div class="product-footer">
                <div class="product-price">
                  <span class="price-value">¥{{ product.price }}</span>
                  <span class="price-unit">/{{ product.unit }}</span>
                </div>
                <button class="product-action">
                  <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M5 8h10M5 8l2 10h6l2-10M5 8V6a2 2 0 012-2h6a2 2 0 012 2v2"/>
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="section-more">
          <button class="btn-link" @click="goTo('/market')">
            查看更多产品
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 8h10M10 5l3 3-3 3"/>
            </svg>
          </button>
        </div>
      </div>
    </section>

    <!-- Financing Section -->
    <section id="financing" class="financing-section">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-eyebrow">金融支持</div>
          <h2 class="section-title">融资服务</h2>
          <p class="section-subtitle">与多家银行合作，为农户提供便捷的融资渠道</p>
        </div>

        <div class="financing-grid">
          <div v-for="item in financingItems" :key="item.id" class="financing-card">
            <div class="financing-icon">
              <svg :width="40" :height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 2v20M17 5H9.5a3.5 3.5 0 000 7h5a3.5 3.5 0 010 7H6"/>
              </svg>
            </div>
            <h3 class="financing-title">{{ item.title }}</h3>
            <p class="financing-desc">{{ item.description }}</p>
            <div class="financing-features">
              <span v-for="feature in item.features" :key="feature" class="feature-tag">
                {{ feature }}
              </span>
            </div>
            <div class="financing-range">
              <span class="range-label">可贷额度</span>
              <span class="range-value">{{ item.amount }}</span>
            </div>
          </div>
        </div>

        <div class="financing-cta">
          <div class="cta-content">
            <h3>需要融资支持？</h3>
            <p>智能匹配最适合您的融资产品，快速审批，放款便捷</p>
          </div>
          <button class="btn-primary" @click="handleFinancingClick">
            申请融资
          </button>
        </div>
      </div>
    </section>

    <!-- Experts Section -->
    <section id="experts" class="experts-section">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-eyebrow">专业指导</div>
          <h2 class="section-title">专家助力</h2>
          <p class="section-subtitle">农业专家在线答疑，为您提供专业的技术指导</p>
        </div>

        <div class="experts-list">
          <div v-for="expert in featuredExperts" :key="expert.id" class="expert-card">
            <div class="expert-avatar">
              <img :src="expert.avatar" :alt="expert.name" />
              <span class="expert-status" :class="{ online: expert.online }"></span>
            </div>
            <div class="expert-info">
              <h3 class="expert-name">{{ expert.name }}</h3>
              <p class="expert-title">{{ expert.title }}</p>
              <p class="expert-specialty">{{ expert.specialty }}</p>
              <div class="expert-stats">
                <span class="stat-item">
                  <svg width="14" height="14" viewBox="0 0 14 14" fill="currentColor">
                    <path d="M7 2l1.5 3.5L12 7l-3 2.5L10.5 13 7 11 3.5 13 4.5 9.5l-3-2.5 3.5-1.5L7 2z"/>
                  </svg>
                  {{ expert.rating }}
                </span>
                <span class="stat-item">{{ expert.answers }} 回答</span>
              </div>
            </div>
            <button class="expert-action" @click="handleExpertClick(expert)">
              咨询
            </button>
          </div>
        </div>

        <div class="section-more">
          <button class="btn-link" @click="goTo('/expert-help')">
            查看所有专家
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 8h10M10 5l3 3-3 3"/>
            </svg>
          </button>
        </div>
      </div>
    </section>

    <!-- Knowledge Section -->
    <section id="knowledge" class="knowledge-section">
      <div class="section-inner">
        <div class="section-header">
          <div class="section-eyebrow">知识库</div>
          <h2 class="section-title">农业知识与问答</h2>
          <p class="section-subtitle">专业农业知识分享，专家在线答疑解惑</p>
        </div>

        <div class="knowledge-grid">
          <!-- Knowledge Articles -->
          <div class="knowledge-articles">
            <h3 class="subsection-title">最新文章</h3>
            <div class="article-list">
              <div v-for="article in knowledgeArticles" :key="article.id"
                   class="article-card" @click="viewArticle(article)">
                <div class="article-image">
                  <img :src="article.image" :alt="article.title" />
                </div>
                <div class="article-content">
                  <span class="article-category">{{ article.category }}</span>
                  <h4 class="article-title">{{ article.title }}</h4>
                  <p class="article-excerpt">{{ article.excerpt }}</p>
                  <div class="article-meta">
                    <span>{{ article.author }}</span>
                    <span>{{ article.date }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Q&A -->
          <div class="knowledge-qa">
            <h3 class="subsection-title">热门问答</h3>
            <div class="qa-list">
              <div v-for="qa in featuredQA" :key="qa.id" class="qa-card" @click="viewQA(qa)">
                <div class="qa-status" :class="{ answered: qa.answered }">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                    <circle cx="8" cy="4" r="2"/>
                    <path d="M4 8a4 4 0 018 0v2H4V8z"/>
                  </svg>
                </div>
                <div class="qa-content">
                  <h4 class="qa-title">{{ qa.title }}</h4>
                  <p class="qa-question">{{ qa.question }}</p>
                  <div class="qa-meta">
                    <span class="qa-expert">{{ qa.expert }}</span>
                    <span class="qa-answers">{{ qa.answers }} 个回答</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="section-more">
          <button class="btn-link" @click="goTo('/expert-help')">
            浏览更多知识
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 8h10M10 5l3 3-3 3"/>
            </svg>
          </button>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="cta-inner">
        <div class="cta-content">
          <h2 class="cta-title">开始您的农业之旅</h2>
          <p class="cta-subtitle">无论您是农户还是买家，我们都能为您提供专业的服务</p>
        </div>
        <div class="cta-actions">
          <button class="btn-cta btn-cta-primary" @click="goTo('/register')">
            免费注册
          </button>
          <button class="btn-cta btn-cta-secondary" @click="goTo('/market')">
            浏览产品
          </button>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="home-footer">
      <div class="footer-inner">
        <div class="footer-section">
          <div class="footer-logo">
            <span class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                <path d="M12 2v10M12 12c0 4 2 6 4 8M12 12c0 4-2 6-4 8"/>
                <path d="M12 2a3 3 0 013 3c0 1.5-1 2.5-3 3s-3 1.5-3 3a3 3 0 013 3z" fill="currentColor" opacity="0.2"/>
                <path d="M16 8a2 2 0 01-2 2h-4a2 2 0 01-2-2"/>
                <path d="M12 12v5M10 17h4"/>
                <path d="M8 4s2 1 4 1 4-1 4-1"/>
              </svg>
            </span>
            <span>农融汇</span>
          </div>
          <p class="footer-desc">连接农户与市场，助力农业发展</p>
        </div>
        <div class="footer-links">
          <div class="footer-link-group">
            <h4>产品服务</h4>
            <a href="#products">农产品交易</a>
            <a href="#financing">融资服务</a>
            <a href="#experts">专家咨询</a>
          </div>
          <div class="footer-link-group">
            <h4>关于我们</h4>
            <a href="#">平台介绍</a>
            <a href="#">联系我们</a>
            <a href="#">帮助中心</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; 2024 农融汇. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

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

// 页面跳转
const goTo = (path) => {
  router.push(path)
}

// 滚动到指定区域
const scrollToSection = (id) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}

// 模拟数据 - 精选产品
const featuredProducts = ref([
  {
    id: 1,
    name: '五常有机大米',
    category: '粮食',
    origin: '黑龙江·五常',
    price: '5.8',
    unit: '斤',
    image: 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400&h=300&fit=crop',
    badge: '优选'
  },
  {
    id: 2,
    name: '寿光新鲜蔬菜',
    category: '蔬菜',
    origin: '山东·寿光',
    price: '3.5',
    unit: '斤',
    image: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=300&fit=crop',
    badge: '直供'
  },
  {
    id: 3,
    name: '烟台红富士苹果',
    category: '水果',
    origin: '山东·烟台',
    price: '8.5',
    unit: '斤',
    image: 'https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=400&h=300&fit=crop',
    badge: null
  },
  {
    id: 4,
    name: '优质散养土鸡',
    category: '畜禽',
    origin: '湖南·湘西',
    price: '45',
    unit: '只',
    image: 'https://images.unsplash.com/photo-1589974593766-7ac39ecdb4e1?w=400&h=300&fit=crop',
    badge: '生态'
  }
])

// 融资项目
const financingItems = ref([
  {
    id: 1,
    title: '农户种植贷款',
    description: '为种植户提供生产资金支持，覆盖种子、化肥、农药等投入',
    features: ['低利率', '手续简便', '放款快'],
    amount: '5-50万'
  },
  {
    id: 2,
    title: '农业设施贷款',
    description: '支持建设大棚、灌溉系统、仓储等农业基础设施',
    features: ['期限长', '额度高', '可循环'],
    amount: '10-100万'
  },
  {
    id: 3,
    title: '农产品收购贷款',
    description: '为收购商提供季节性资金支持，助力农产品流通',
    features: ['随借随还', '灵活使用'],
    amount: '20-200万'
  }
])

// 专家列表
const featuredExperts = ref([
  {
    id: 1,
    name: '李建国',
    title: '高级农艺师',
    specialty: '水稻种植与病虫害防治',
    avatar: 'https://images.unsplash.com/photo-1560250097-0b93528c311a?w=100&h=100&fit=crop',
    rating: 4.9,
    answers: 328,
    online: true
  },
  {
    id: 2,
    name: '王淑芬',
    title: '蔬菜栽培专家',
    specialty: '温室蔬菜栽培技术',
    avatar: 'https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=100&h=100&fit=crop',
    rating: 4.8,
    answers: 256,
    online: true
  },
  {
    id: 3,
    name: '张明远',
    title: '土壤肥料专家',
    specialty: '土壤改良与科学施肥',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop',
    rating: 4.9,
    answers: 189,
    online: false
  },
  {
    id: 4,
    name: '赵海燕',
    title: '果树栽培专家',
    specialty: '果树修剪与病虫害防治',
    avatar: 'https://images.unsplash.com/photo-1607746882042-944635dfe10e?w=100&h=100&fit=crop',
    rating: 4.7,
    answers: 143,
    online: false
  }
])

// 知识文章
const knowledgeArticles = ref([
  {
    id: 1,
    title: '春季小麦田间管理技术要点',
    excerpt: '详细介绍春季小麦返青后的田间管理措施，包括施肥、浇水、病虫害防治等关键技术...',
    category: '粮食作物',
    image: 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=300&h=200&fit=crop',
    author: '李建国',
    date: '2024-03-15'
  },
  {
    id: 2,
    title: '温室蔬菜病虫害绿色防控技术',
    excerpt: '介绍温室蔬菜主要病虫害的识别方法及绿色防控技术，减少农药使用，提高蔬菜品质...',
    category: '蔬菜种植',
    image: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=300&h=200&fit=crop',
    author: '王淑芬',
    date: '2024-03-12'
  }
])

// 问答
const featuredQA = ref([
  {
    id: 1,
    title: '水稻稻瘟病如何防治？',
    question: '我的水稻出现了稻瘟病，请问应该用什么药？什么时候喷药效果最好？',
    expert: '李建国',
    answered: true,
    answers: 3
  },
  {
    id: 2,
    title: '番茄脐腐病怎么解决？',
    question: '温室种植的番茄经常出现脐腐病，是缺钙吗？应该如何预防和治疗？',
    expert: '王淑芬',
    answered: true,
    answers: 5
  },
  {
    id: 3,
    title: '苹果树什么时候修剪最好？',
    question: '请问苹果树的最佳修剪时间是什么时候？应该如何修剪才能提高产量？',
    expert: '赵海燕',
    answered: true,
    answers: 2
  }
])

// 点击处理
const viewProductDetail = (product) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  // TODO: 跳转到产品详情
  ElMessage.info('查看产品详情：' + product.name)
}

const handleFinancingClick = () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  if (userStore.role === 'farmer') {
    router.push('/farmer/finance')
  } else {
    ElMessage.info('融资服务仅对农户开放')
  }
}

const handleExpertClick = (expert) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  // TODO: 跳转到专家详情或发起咨询
  ElMessage.info('咨询专家：' + expert.name)
}

const viewArticle = (article) => {
  ElMessage.info('查看文章：' + article.title)
}

const viewQA = (qa) => {
  ElMessage.info('查看问答：' + qa.title)
}

onMounted(() => {
  // TODO: 从后端API加载数据
  // loadHomeData()
})
</script>

<style scoped>
/* ===== 全局变量扩展 ===== */
.home-page {
  --color-primary: #2D5A3D;
  --color-primary-light: #4A7C59;
  --color-secondary: #C9A661;
  --color-accent: #B85C38;
  --color-bg: #F7F5F0;
  --color-text: #1F2923;
  --color-text-light: #6B7280;
  --color-border: #E5E0D8;
  --color-white: #FFFFFF;
  --font-display: 'Noto Serif SC', serif;
  --font-body: 'Noto Sans SC', sans-serif;
}

/* ===== Header ===== */
.home-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 72px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--color-border);
  z-index: 1000;
}

.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-icon {
  width: 36px;
  height: 36px;
  color: var(--color-primary);
}

.logo-text {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 600;
  color: var(--color-primary);
}

.main-nav {
  display: flex;
  gap: 40px;
}

.main-nav a {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-light);
  transition: color 0.2s;
  position: relative;
}

.main-nav a:hover {
  color: var(--color-primary);
}

.main-nav a::after {
  content: '';
  position: absolute;
  bottom: -28px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-primary);
  transform: scaleX(0);
  transition: transform 0.2s;
}

.main-nav a:hover::after {
  transform: scaleX(1);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-text {
  padding: 8px 20px;
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text);
  background: transparent;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
}

.btn-text:hover {
  color: var(--color-primary);
}

.btn-primary {
  padding: 10px 24px;
  font-size: 15px;
  font-weight: 500;
  color: var(--color-white);
  background: var(--color-primary);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: var(--color-primary-dark, #1F4229);
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--color-bg);
  border-radius: 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.user-chip:hover {
  background: var(--color-border);
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

/* ===== Hero Section ===== */
.hero-section {
  position: relative;
  padding-top: 72px;
  background: linear-gradient(180deg, #EBF5EE 0%, var(--color-bg) 100%);
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.hero-pattern {
  position: absolute;
  top: 0;
  right: 0;
  width: 60%;
  height: 100%;
  background-image:
    radial-gradient(circle at 20% 50%, rgba(45, 90, 61, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(201, 166, 97, 0.08) 0%, transparent 50%);
}

.hero-content {
  position: relative;
  max-width: 1280px;
  margin: 0 auto;
  padding: 80px 32px 60px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.hero-text {
  max-width: 560px;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 52px;
  font-weight: 700;
  line-height: 1.1;
  color: var(--color-text);
  margin: 0 0 24px;
}

.title-line {
  display: block;
}

.title-accent {
  color: var(--color-primary);
}

.hero-subtitle {
  font-size: 18px;
  line-height: 1.6;
  color: var(--color-text-light);
  margin: 0 0 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.btn-hero {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-hero-primary {
  color: var(--color-white);
  background: var(--color-primary);
  border: none;
}

.btn-hero-primary:hover {
  background: var(--color-primary-dark, #1F4229);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(45, 90, 61, 0.3);
}

.btn-hero-secondary {
  color: var(--color-primary);
  background: transparent;
  border: 2px solid var(--color-primary);
}

.btn-hero-secondary:hover {
  background: rgba(45, 90, 61, 0.1);
}

.hero-visual {
  position: relative;
  height: 400px;
}

.visual-card {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px;
  background: var(--color-white);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
}

.visual-card:hover {
  transform: translateY(-8px);
}

.visual-card-1 {
  top: 20%;
  left: 10%;
  animation: float 6s ease-in-out infinite;
}

.visual-card-2 {
  top: 40%;
  right: 15%;
  animation: float 6s ease-in-out infinite 2s;
}

.visual-card-3 {
  bottom: 10%;
  left: 30%;
  animation: float 6s ease-in-out infinite 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.card-icon {
  width: 64px;
  height: 64px;
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(45, 90, 61, 0.1);
  border-radius: 16px;
}

.card-icon svg {
  width: 36px;
  height: 36px;
}

.card-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--color-text);
}

.trust-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48px;
  padding: 32px;
  background: var(--color-white);
  border-top: 1px solid var(--color-border);
}

.trust-item {
  text-align: center;
}

.trust-value {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary);
}

.trust-label {
  font-size: 14px;
  color: var(--color-text-light);
  margin-top: 4px;
}

.trust-divider {
  width: 1px;
  height: 40px;
  background: var(--color-border);
}

/* ===== Sections Common ===== */
.products-section,
.financing-section,
.experts-section,
.knowledge-section {
  padding: 80px 32px;
}

.section-inner {
  max-width: 1280px;
  margin: 0 auto;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-eyebrow {
  display: inline-block;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: var(--color-secondary);
  margin-bottom: 12px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 700;
  color: var(--color-text);
  margin: 0 0 12px;
}

.section-subtitle {
  font-size: 16px;
  color: var(--color-text-light);
  margin: 0;
}

.section-more {
  text-align: center;
  margin-top: 32px;
}

.btn-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: var(--color-primary);
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 8px 16px;
  transition: gap 0.2s;
}

.btn-link:hover {
  gap: 12px;
}

/* ===== Products Section ===== */
.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.product-card {
  background: var(--color-white);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}

.product-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 500;
  color: var(--color-white);
  background: var(--color-accent);
  border-radius: 4px;
}

.product-content {
  padding: 20px;
}

.product-category {
  font-size: 12px;
  font-weight: 500;
  color: var(--color-secondary);
  margin-bottom: 8px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-origin {
  font-size: 14px;
  color: var(--color-text-light);
  margin: 0 0 16px;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  display: flex;
  align-items: baseline;
}

.price-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--color-accent);
}

.price-unit {
  font-size: 14px;
  color: var(--color-text-light);
}

.product-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  color: var(--color-primary);
  background: rgba(45, 90, 61, 0.1);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.product-action:hover {
  background: var(--color-primary);
  color: var(--color-white);
}

/* ===== Financing Section ===== */
.financing-section {
  background: linear-gradient(180deg, rgba(74, 124, 89, 0.05) 0%, transparent 100%);
}

.financing-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 48px;
}

.financing-card {
  padding: 32px;
  background: var(--color-white);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  transition: all 0.3s;
}

.financing-card:hover {
  border-color: var(--color-secondary);
  box-shadow: 0 8px 24px rgba(201, 166, 97, 0.15);
}

.financing-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  color: var(--color-secondary);
  background: rgba(201, 166, 97, 0.1);
  border-radius: 12px;
  margin-bottom: 20px;
}

.financing-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 12px;
}

.financing-desc {
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text-light);
  margin: 0 0 16px;
}

.financing-features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.feature-tag {
  padding: 4px 12px;
  font-size: 12px;
  color: var(--color-primary);
  background: rgba(45, 90, 61, 0.1);
  border-radius: 4px;
}

.financing-range {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid var(--color-border);
}

.range-label {
  font-size: 14px;
  color: var(--color-text-light);
}

.range-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-accent);
}

.financing-cta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32px;
  background: var(--color-primary);
  border-radius: 12px;
  color: var(--color-white);
}

.cta-content h3 {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 8px;
}

.cta-content p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.financing-cta .btn-primary {
  background: var(--color-white);
  color: var(--color-primary);
}

.financing-cta .btn-primary:hover {
  background: rgba(255, 255, 255, 0.9);
}

/* ===== Experts Section ===== */
.experts-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.expert-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: var(--color-white);
  border-radius: 12px;
  border: 1px solid var(--color-border);
  cursor: pointer;
  transition: all 0.3s;
}

.expert-card:hover {
  border-color: var(--color-primary-light);
  box-shadow: 0 4px 16px rgba(45, 90, 61, 0.1);
}

.expert-avatar {
  position: relative;
  width: 64px;
  height: 64px;
  flex-shrink: 0;
}

.expert-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.expert-status {
  position: absolute;
  bottom: 4px;
  right: 4px;
  width: 12px;
  height: 12px;
  background: var(--color-text-light);
  border: 2px solid var(--color-white);
  border-radius: 50%;
}

.expert-status.online {
  background: #10B981;
}

.expert-info {
  flex: 1;
}

.expert-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 4px;
}

.expert-title {
  font-size: 13px;
  color: var(--color-secondary);
  margin: 0 0 8px;
}

.expert-specialty {
  font-size: 14px;
  color: var(--color-text-light);
  margin: 0 0 12px;
}

.expert-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--color-accent);
}

.expert-action {
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
  color: var(--color-white);
  background: var(--color-primary);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.expert-action:hover {
  background: var(--color-primary-dark, #1F4229);
}

/* ===== Knowledge Section ===== */
.knowledge-section {
  background: linear-gradient(180deg, rgba(201, 166, 97, 0.05) 0%, transparent 100%);
}

.knowledge-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 48px;
}

.subsection-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--color-secondary);
}

.article-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: var(--color-white);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.article-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.article-image {
  width: 120px;
  height: 80px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 8px;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.article-category {
  font-size: 12px;
  font-weight: 500;
  color: var(--color-secondary);
  margin-bottom: 6px;
}

.article-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-excerpt {
  font-size: 13px;
  color: var(--color-text-light);
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--color-text-light);
}

.qa-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.qa-card {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: var(--color-white);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.qa-card:hover {
  border-color: var(--color-primary);
}

.qa-status {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  color: var(--color-text-light);
  background: var(--color-bg);
  border-radius: 50%;
  flex-shrink: 0;
}

.qa-status.answered {
  color: var(--color-primary);
  background: rgba(45, 90, 61, 0.1);
}

.qa-content {
  flex: 1;
}

.qa-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text);
  margin: 0 0 6px;
}

.qa-question {
  font-size: 13px;
  color: var(--color-text-light);
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.qa-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
}

.qa-expert {
  color: var(--color-secondary);
}

.qa-answers {
  color: var(--color-text-light);
}

/* ===== CTA Section ===== */
.cta-section {
  padding: 80px 32px;
  background: var(--color-primary);
  color: var(--color-white);
}

.cta-inner {
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
}

.cta-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 16px;
}

.cta-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 32px;
}

.cta-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
}

.btn-cta {
  padding: 14px 40px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cta-primary {
  color: var(--color-primary);
  background: var(--color-white);
  border: none;
}

.btn-cta-primary:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
}

.btn-cta-secondary {
  color: var(--color-white);
  background: transparent;
  border: 2px solid var(--color-white);
}

.btn-cta-secondary:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* ===== Footer ===== */
.home-footer {
  padding: 48px 32px 24px;
  background: var(--color-text);
  color: var(--color-white);
}

.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  margin-bottom: 32px;
}

.footer-section {
  max-width: 300px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

.footer-logo .logo-icon {
  width: 32px;
  height: 32px;
  color: var(--color-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-logo .logo-icon svg {
  width: 100%;
  height: 100%;
}

.footer-desc {
  font-size: 14px;
  opacity: 0.8;
  line-height: 1.6;
  margin: 0;
}

.footer-links {
  display: flex;
  gap: 64px;
}

.footer-link-group h4 {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 16px;
}

.footer-link-group a {
  display: block;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  margin-bottom: 12px;
  transition: color 0.2s;
}

.footer-link-group a:hover {
  color: var(--color-white);
}

.footer-bottom {
  max-width: 1280px;
  margin: 0 auto;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.footer-bottom p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .financing-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .experts-list {
    grid-template-columns: 1fr;
  }

  .knowledge-grid {
    grid-template-columns: 1fr;
  }

  .hero-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .hero-visual {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-inner {
    padding: 0 20px;
  }

  .main-nav {
    display: none;
  }

  .products-section,
  .financing-section,
  .experts-section,
  .knowledge-section {
    padding: 48px 20px;
  }

  .section-title {
    font-size: 28px;
  }

  .hero-title {
    font-size: 36px;
  }

  .products-grid {
    grid-template-columns: 1fr;
  }

  .financing-grid {
    grid-template-columns: 1fr;
  }

  .financing-cta {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .cta-actions {
    flex-direction: column;
  }

  .trust-bar {
    flex-wrap: wrap;
    gap: 24px;
  }

  .trust-divider {
    display: none;
  }

  .footer-inner {
    flex-direction: column;
    gap: 32px;
  }
}

/* Element Plus Dropdown Override */
:deep(.el-dropdown-menu) {
  font-family: var(--font-body);
}

:deep(.el-dropdown-menu__item) {
  font-size: 14px;
}
</style>
