<template>
  <div class="market-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">农产品交易</h1>
        <p class="page-subtitle">连接农户与买家，优质农产品直接对接</p>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="page-content">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="filter-group">
          <label class="filter-label">交易类型</label>
          <div class="type-toggle">
            <button
              :class="['type-btn', { active: filterType === '' }]"
              @click="setFilterType('')"
            >
              全部
            </button>
            <button
              :class="['type-btn', { active: filterType === 'source' }]"
              @click="setFilterType('source')"
            >
              <span class="type-icon">🌾</span>
              货源
            </button>
            <button
              :class="['type-btn', { active: filterType === 'demand' }]"
              @click="setFilterType('demand')"
            >
              <span class="type-icon">📋</span>
              需求
            </button>
          </div>
        </div>

        <div class="filter-group">
          <label class="filter-label">类别</label>
          <select v-model="categoryFilter" class="filter-select" @change="loadProducts">
            <option value="">全部类别</option>
            <option value="粮食">粮食</option>
            <option value="蔬菜">蔬菜</option>
            <option value="水果">水果</option>
            <option value="畜禽">畜禽</option>
            <option value="水产">水产</option>
            <option value="其他">其他</option>
          </select>
        </div>

        <div class="filter-group filter-search">
          <input
            v-model="keyword"
            type="text"
            class="search-input"
            placeholder="搜索商品名称、产地..."
            @keyup.enter="loadProducts"
          />
          <button class="search-btn" @click="loadProducts">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="9" cy="9" r="7"/>
              <path d="M13 13l4 4"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- 商品列表 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="productList.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="12" cy="12" r="10"/>
            <path d="M12 8v4M12 16h.01"/>
          </svg>
        </div>
        <h3>暂无商品</h3>
        <p>请调整筛选条件或稍后再来</p>
      </div>

      <div v-else class="product-grid">
        <div
          v-for="product in productList"
          :key="product.orderId"
          class="product-card"
          @click="handleViewDetail(product)"
        >
          <div class="product-image">
            <img :src="product.picture || defaultImage" :alt="product.title" />
            <span v-if="product.type === 'demand'" class="type-badge demand">需求</span>
            <span v-else class="type-badge source">货源</span>
          </div>
          <div class="product-content">
            <div class="product-category">{{ product.category || '农产品' }}</div>
            <h3 class="product-name">{{ product.title }}</h3>
            <p class="product-origin">{{ product.origin || '产地信息' }}</p>
            <div class="product-footer">
              <div class="product-price">
                <template v-if="product.type === 'source' && product.price">
                  <span class="price-value">¥{{ product.price }}</span>
                  <span class="price-unit">/{{ product.unit || '斤' }}</span>
                </template>
                <template v-else>
                  <span class="price-negotiable">面议</span>
                </template>
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

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination-wrapper">
        <div class="pagination-info">
          共 {{ total }} 条记录，第 {{ currentPage }} / {{ totalPages }} 页
        </div>
        <div class="pagination">
          <button
            class="page-btn"
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            上一页
          </button>
          <button
            v-for="page in visiblePages"
            :key="page"
            :class="['page-num', { active: page === currentPage }]"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>
          <button
            class="page-btn"
            :disabled="currentPage === totalPages"
            @click="goToPage(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 商品详情对话框 -->
    <div v-if="detailVisible && currentProduct" class="modal-overlay" @click="closeDetail">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ currentProduct.title }}</h2>
          <button class="modal-close" @click="closeDetail">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M6 6l12 12M6 18L18 6"/>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div class="detail-image">
            <img :src="currentProduct.picture || defaultImage" :alt="currentProduct.title" />
          </div>
          <div class="detail-info">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">类别</span>
                <span class="info-value">{{ currentProduct.category || '农产品' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">产地</span>
                <span class="info-value">{{ currentProduct.origin || '暂无' }}</span>
              </div>
              <div class="info-item" v-if="currentProduct.type === 'source' && currentProduct.price">
                <span class="info-label">价格</span>
                <span class="info-value price">¥{{ currentProduct.price }}/{{ currentProduct.unit || '斤' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">发布人</span>
                <span class="info-value">{{ currentProduct.ownName || '农户' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">发布时间</span>
                <span class="info-value">{{ formatDate(currentProduct.createTime) }}</span>
              </div>
            </div>
            <div class="detail-description">
              <h4>商品描述</h4>
              <p>{{ currentProduct.content || '暂无描述' }}</p>
            </div>
          </div>
        </div>
        <div class="modal-footer" v-if="userStore.role === 'buyer' && currentProduct.type === 'source'">
          <button class="btn-primary" @click="handleAddToCart">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="9" cy="21" r="1"/>
              <circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/>
            </svg>
            加入购物车
          </button>
          <button class="btn-secondary" @click="handleBuyNow">
            立即购买
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

// 筛选状态
const filterType = ref('')
const categoryFilter = ref('')
const keyword = ref('')

// 分页状态
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 商品数据
const productList = ref([])
const loading = ref(false)

// 详情弹窗
const detailVisible = ref(false)
const currentProduct = ref(null)

// 默认图片
const defaultImage = 'https://images.unsplash.com/photo-1500937386664-56d1dfef3854?w=400&h=300&fit=crop'

// 设置筛选类型
const setFilterType = (type) => {
  filterType.value = type
  currentPage.value = 1
  loadProducts()
}

// 加载商品列表
const loadProducts = async () => {
  loading.value = true
  try {
    const response = await api.get('/product/page', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        type: filterType.value,
        category: categoryFilter.value,
        keyword: keyword.value
      }
    })

    if (response.code === 200) {
      productList.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '加载商品列表失败')
    }
  } catch (error) {
    console.error('加载商品列表失败:', error)
    // 使用模拟数据
    productList.value = [
      {
        orderId: 1,
        title: '五常有机大米',
        category: '粮食',
        price: '5.8',
        unit: '斤',
        origin: '黑龙江·五常',
        type: 'source',
        ownName: '张三农场',
        content: '优质有机大米，无农药化肥，口感香甜',
        picture: 'https://images.unsplash.com/photo-1586201375761-83865001e31c?w=400&h=300&fit=crop',
        createTime: '2024-01-15 10:00:00'
      },
      {
        orderId: 2,
        title: '寿光新鲜蔬菜',
        category: '蔬菜',
        price: '3.5',
        unit: '斤',
        origin: '山东·寿光',
        type: 'source',
        ownName: '李四合作社',
        content: '新鲜时令蔬菜，产地直供',
        picture: 'https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=300&fit=crop',
        createTime: '2024-01-14 14:30:00'
      },
      {
        orderId: 3,
        title: '求购优质小麦',
        category: '粮食',
        origin: '河南·郑州',
        type: 'demand',
        ownName: '王五贸易',
        content: '求购优质小麦100吨，要求水分≤13%',
        picture: 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&h=300&fit=crop',
        createTime: '2024-01-13 09:00:00'
      }
    ]
    total.value = 3
  } finally {
    loading.value = false
  }
}

// 计算可见页码
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 跳转到指定页
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadProducts()
    // 滚动到顶部
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 查看详情
const handleViewDetail = (product) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }
  currentProduct.value = product
  detailVisible.value = true
}

// 关闭详情
const closeDetail = () => {
  detailVisible.value = false
  currentProduct.value = null
}

// 加入购物车
const handleAddToCart = async () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录')
    router.push('/login')
    return
  }

  try {
    const response = await api.post('/cart', {
      orderId: currentProduct.value.orderId,
      count: 1
    })

    if (response.code === 200) {
      ElMessage.success('已加入购物车')
      closeDetail()
    } else {
      ElMessage.error(response.message || '加入购物车失败')
    }
  } catch (error) {
    ElMessage.error('加入购物车失败')
  }
}

// 立即购买
const handleBuyNow = () => {
  ElMessage.info('立即购买功能开发中')
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '暂无'
  return dateStr.split(' ')[0]
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.market-page {
  min-height: 100vh;
  background: var(--color-bg);
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-dark) 100%);
  color: var(--color-white);
  padding: var(--space-3xl) 0;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 60%;
  height: 100%;
  background: radial-gradient(circle at 30% 50%, rgba(201, 166, 97, 0.15) 0%, transparent 50%);
  pointer-events: none;
}

.header-content {
  position: relative;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 var(--space-lg);
  text-align: center;
}

.page-title {
  font-family: var(--font-display);
  font-size: var(--text-5xl);
  font-weight: var(--font-bold);
  margin: 0 0 var(--space-md);
  color: var(--color-white);
}

.page-subtitle {
  font-size: var(--text-lg);
  opacity: 0.9;
  margin: 0;
}

/* 主内容区 */
.page-content {
  max-width: 1280px;
  margin: -40px auto 0;
  padding: 0 var(--space-lg) var(--space-2xl);
  position: relative;
}

/* 筛选栏 */
.filter-bar {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  padding: var(--space-lg);
  background: var(--color-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  margin-bottom: var(--space-lg);
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.filter-label {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-text-secondary);
  white-space: nowrap;
}

.type-toggle {
  display: flex;
  gap: var(--space-xs);
}

.type-btn {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  padding: var(--space-sm) var(--space-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-text);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
}

.type-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.type-btn.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: var(--color-white);
}

.type-icon {
  font-size: 16px;
}

.filter-select {
  padding: var(--space-sm) var(--space-md);
  font-size: var(--text-sm);
  color: var(--color-text);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: border-color var(--transition-base);
  min-width: 120px;
}

.filter-select:hover {
  border-color: var(--color-primary);
}

.filter-select:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(45, 90, 61, 0.1);
}

.filter-search {
  flex: 1;
  margin-left: auto;
}

.search-input {
  flex: 1;
  padding: var(--space-sm) var(--space-md);
  font-size: var(--text-sm);
  color: var(--color-text);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md) 0 0 var(--radius-md);
  transition: border-color var(--transition-base);
}

.search-input:focus {
  outline: none;
  border-color: var(--color-primary);
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 38px;
  background: var(--color-primary);
  border: none;
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  cursor: pointer;
  color: var(--color-white);
  transition: background var(--transition-base);
}

.search-btn:hover {
  background: var(--color-primary-dark);
}

/* 加载和空状态 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-3xl);
  text-align: center;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--color-border);
  border-top-color: var(--color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p,
.empty-state p {
  margin-top: var(--space-md);
  color: var(--color-text-secondary);
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: var(--color-text-light);
  opacity: 0.5;
}

.empty-icon svg {
  width: 100%;
  height: 100%;
}

.empty-state h3 {
  margin: var(--space-md) 0 var(--space-sm);
  color: var(--color-text);
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-lg);
  margin-bottom: var(--space-xl);
}

.product-card {
  background: var(--color-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-sm);
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: var(--shadow-lg);
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
  transition: transform var(--transition-base);
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.type-badge {
  position: absolute;
  top: var(--space-sm);
  left: var(--space-sm);
  padding: var(--space-xs) var(--space-sm);
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--color-white);
  border-radius: var(--radius-sm);
}

.type-badge.source {
  background: var(--color-success);
}

.type-badge.demand {
  background: var(--color-warning);
}

.product-content {
  padding: var(--space-md);
}

.product-category {
  font-size: var(--text-xs);
  font-weight: var(--font-medium);
  color: var(--color-secondary);
  margin-bottom: var(--space-xs);
}

.product-name {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  color: var(--color-text);
  margin: 0 0 var(--space-xs);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-origin {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  margin: 0 0 var(--space-md);
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
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--color-accent);
}

.price-unit {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  margin-left: var(--space-xs);
}

.price-negotiable {
  font-size: var(--text-lg);
  font-weight: var(--font-medium);
  color: var(--color-warning);
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
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
}

.product-card:hover .product-action {
  background: var(--color-primary);
  color: var(--color-white);
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-lg);
  background: var(--color-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
}

.pagination-info {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

.pagination {
  display: flex;
  gap: var(--space-xs);
}

.page-btn,
.page-num {
  padding: var(--space-sm) var(--space-md);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-text);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-num.active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: var(--color-white);
}

.page-btn:not(:disabled):hover,
.page-num:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

/* 详情弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: var(--z-modal);
  padding: var(--space-lg);
}

.modal-content {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-lg);
  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  font-size: var(--text-2xl);
  font-weight: var(--font-semibold);
  color: var(--color-text);
  margin: 0;
}

.modal-close {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-secondary);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
}

.modal-close:hover {
  color: var(--color-text);
  background: var(--color-bg);
}

.modal-body {
  padding: var(--space-lg);
}

.detail-image {
  margin-bottom: var(--space-lg);
}

.detail-image img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: var(--radius-lg);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: var(--space-sm) 0;
  border-bottom: 1px solid var(--color-border);
}

.info-label {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

.info-value {
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
  color: var(--color-text);
}

.info-value.price {
  color: var(--color-accent);
  font-size: var(--text-lg);
}

.detail-description h4 {
  font-size: var(--text-base);
  font-weight: var(--font-semibold);
  margin-bottom: var(--space-sm);
}

.detail-description p {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.modal-footer {
  display: flex;
  gap: var(--space-md);
  padding: var(--space-lg);
  border-top: 1px solid var(--color-border);
}

.btn-primary,
.btn-secondary {
  padding: var(--space-md) var(--space-xl);
  font-size: var(--text-base);
  font-weight: var(--font-medium);
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-base);
}

.btn-primary {
  color: var(--color-white);
  background: var(--color-primary);
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.btn-primary:hover {
  background: var(--color-primary-dark);
}

.btn-secondary {
  color: var(--color-primary);
  background: transparent;
  border: 1.5px solid var(--color-primary);
}

.btn-secondary:hover {
  background: rgba(45, 90, 61, 0.1);
}

/* 响应式 */
@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}

@media (max-width: 768px) {
  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-search {
    margin-left: 0;
  }

  .search-input {
    width: 100%;
  }

  .product-grid {
    grid-template-columns: 1fr;
  }

  .pagination-wrapper {
    flex-direction: column;
    gap: var(--space-md);
  }

  .pagination {
    width: 100%;
    justify-content: center;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .modal-footer {
    flex-direction: column;
  }
}
</style>
