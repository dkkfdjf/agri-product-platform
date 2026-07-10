<template>
  <div v-loading="loading" class="product-detail-page">
    <div v-if="product" class="detail-container">
      <!-- 返回按钮 -->
      <div class="back-section">
        <el-button :icon="ArrowLeft" @click="goBack">返回列表</el-button>
      </div>

      <!-- 商品详情主体 -->
      <div class="detail-main">
        <!-- 左侧图片区 -->
        <div class="image-section">
          <div class="main-image">
            <img
              :src="product.picPath || '/placeholder.jpg'"
              :alt="product.name"
              @error="handleImageError"
            />
            <div v-if="product.stock <= 0" class="out-of-stock-badge">缺货</div>
          </div>
        </div>

        <!-- 右侧信息区 -->
        <div class="info-section">
          <h1 class="product-name">{{ product.name }}</h1>
          <div class="product-meta">
            <span class="product-id">商品编号: {{ product.productId }}</span>
          </div>

          <div class="price-section">
            <span class="price-label">价格</span>
            <span class="price-value">¥{{ product.price }}</span>
            <span class="price-unit">/ kg</span>
          </div>

          <div class="stock-section">
            <span class="stock-label">库存</span>
            <span class="stock-value" :class="{ low: product.stock < 10 }">
              {{ product.stock }} 件
            </span>
            <span v-if="product.stock < 10" class="stock-warning">库存紧张</span>
          </div>

          <div class="divider"></div>

          <div class="description-section">
            <h3 class="section-title">商品描述</h3>
            <p class="description">{{ product.description }}</p>
          </div>

          <div class="action-section">
            <div class="quantity-selector">
              <span class="quantity-label">数量：</span>
              <el-input-number
                v-model="quantity"
                :min="1"
                :max="product.stock"
                size="large"
              />
            </div>
            <div class="action-buttons">
              <el-button
                type="primary"
                size="large"
                :disabled="product.stock <= 0"
                @click="addToCart"
              >
                <el-icon><ShoppingCart /></el-icon>
                加入购物车
              </el-button>
              <el-button
                type="success"
                size="large"
                :disabled="product.stock <= 0"
                @click="buyNow"
              >
                立即购买
              </el-button>
            </div>
          </div>

          <!-- 卖家信息 -->
          <div class="seller-section">
            <div class="seller-info">
              <span class="seller-label">卖家：</span>
              <span class="seller-name">{{ product.farmerName || '农户' }}</span>
            </div>
            <el-button type="text" size="small">联系卖家</el-button>
          </div>
        </div>
      </div>

      <!-- 商品详情/评价标签页 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="商品详情" name="detail">
            <div class="tab-content">
              <div class="detail-item">
                <h4>商品名称</h4>
                <p>{{ product.name }}</p>
              </div>
              <div class="detail-item">
                <h4>商品描述</h4>
                <p>{{ product.description }}</p>
              </div>
              <div class="detail-item">
                <h4>价格</h4>
                <p>¥{{ product.price }} / kg</p>
              </div>
              <div class="detail-item">
                <h4>库存</h4>
                <p>{{ product.stock }} 件</p>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品评价" name="reviews">
            <div class="reviews-content">
              <div class="review-summary">
                <div class="rating-score">4.8</div>
                <div class="rating-info">
                  <div class="rating-stars">★★★★★</div>
                  <div class="rating-count">共 128 条评价</div>
                </div>
              </div>
              <div class="review-list">
                <div v-for="i in 3" :key="i" class="review-item">
                  <div class="review-header">
                    <span class="reviewer">买家{{ i }}</span>
                    <span class="review-date">2024-01-{{ 10 + i }}</span>
                  </div>
                  <div class="review-rating">★★★★★</div>
                  <p class="review-text">商品质量很好，包装也很仔细，下次还会再来购买！</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- 加载失败或不存在 -->
    <div v-else-if="!loading" class="not-found">
      <div class="not-found-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
          <circle cx="12" cy="12" r="10"/>
          <line x1="12" y1="8" x2="12" y2="12"/>
          <line x1="12" y1="16" x2="12" y2="16"/>
        </svg>
      </div>
      <h2>商品不存在</h2>
      <el-button type="primary" @click="goBack">返回列表</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ShoppingCart } from '@element-plus/icons-vue'
import { getProductDetail } from '@/api/product'
import { addToCart as addCartItem } from '@/api/cart'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const product = ref(null)
const quantity = ref(1)
const activeTab = ref('detail')

onMounted(() => {
  loadProductDetail()
})

const loadProductDetail = async () => {
  const id = route.params.id
  if (!id) {
    ElMessage.error('商品ID不存在')
    goBack()
    return
  }

  loading.value = true
  try {
    const res = await getProductDetail(id)
    product.value = res.data
    if (product.value && product.value.stock > 0) {
      quantity.value = Math.min(1, product.value.stock)
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    ElMessage.error('加载商品详情失败')
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push('/product')
}

const addToCart = async () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!product.value || product.value.stock <= 0) {
    ElMessage.warning('商品缺货')
    return
  }

  try {
    await addCartItem({
      productId: product.value.productId,
      quantity: quantity.value
    })
    ElMessage.success('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败:', error)
    ElMessage.error(error.message || '加入购物车失败')
  }
}

const buyNow = async () => {
  if (!userStore.isLoggedIn()) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  if (!product.value || product.value.stock <= 0) {
    ElMessage.warning('商品缺货')
    return
  }

  // 先加入购物车
  try {
    await addCartItem({
      productId: product.value.productId,
      quantity: quantity.value
    })
    // 跳转到购物车页面
    router.push('/buyer/cart')
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  }
}

const handleImageError = (e) => {
  e.target.src = '/placeholder.jpg'
}
</script>

<style scoped>
.product-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-8, 32px) var(--spacing-6, 24px);
}

/* 返回按钮 */
.back-section {
  margin-bottom: var(--spacing-4, 16px);
}

/* 详情主体 */
.detail-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-8, 32px);
  margin-bottom: var(--spacing-8, 32px);
}

/* 图片区 */
.image-section {
  position: sticky;
  top: var(--spacing-6, 24px);
  height: fit-content;
}

.main-image {
  position: relative;
  aspect-ratio: 1;
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-lg, 12px);
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.out-of-stock-badge {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  padding: var(--spacing-3, 12px) var(--spacing-6, 24px);
  background: rgba(0, 0, 0, 0.7);
  color: #ffffff;
  font-size: var(--font-size-xl, 24px);
  font-weight: var(--font-weight-bold, 700);
  border-radius: var(--radius-base, 8px);
}

/* 信息区 */
.info-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4, 16px);
}

.product-name {
  font-size: var(--font-size-3xl, 38px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0;
  line-height: 1.3;
}

.product-meta {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
}

/* 价格区 */
.price-section {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-1, 4px);
  padding: var(--spacing-4, 16px);
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-base, 8px);
}

.price-label {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
}

.price-value {
  font-size: var(--font-size-4xl, 48px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-error, #b85c38);
}

.price-unit {
  font-size: var(--font-size-base, 16px);
  color: var(--color-text-tertiary, #6b7280);
}

/* 库存区 */
.stock-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
}

.stock-label {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
}

.stock-value {
  font-size: var(--font-size-base, 16px);
  color: var(--color-success, #4a7c59);
  font-weight: var(--font-weight-semibold, 600);
}

.stock-value.low {
  color: var(--color-warning, #c9a661);
}

.stock-warning {
  padding: var(--spacing-1, 4px) var(--spacing-2, 8px);
  font-size: var(--font-size-xs, 12px);
  color: var(--color-warning, #c9a661);
  background: rgba(201, 166, 97, 0.1);
  border-radius: var(--radius-sm, 4px);
}

.divider {
  height: 1px;
  background: var(--color-border, #e5e0d8);
}

/* 描述区 */
.description-section {
  padding: var(--spacing-4, 16px) 0;
}

.section-title {
  font-size: var(--font-size-lg, 20px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-3, 12px);
}

.description {
  font-size: var(--font-size-base, 16px);
  line-height: 1.6;
  color: var(--color-text-secondary, #4a5249);
  margin: 0;
}

/* 操作区 */
.action-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4, 16px);
  padding: var(--spacing-4, 16px) 0;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: var(--spacing-3, 12px);
}

.quantity-label {
  font-size: var(--font-size-base, 16px);
  color: var(--color-text-secondary, #4a5249);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-3, 12px);
}

.action-buttons .el-button {
  flex: 1;
}

/* 卖家区 */
.seller-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-3, 12px) var(--spacing-4, 16px);
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-base, 8px);
}

.seller-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-2, 8px);
}

.seller-label {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
}

.seller-name {
  font-size: var(--font-size-base, 16px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
}

/* 标签页 */
.detail-tabs {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  overflow: hidden;
}

.tab-content {
  padding: var(--spacing-6, 24px);
}

.detail-item {
  margin-bottom: var(--spacing-6, 24px);
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-item h4 {
  font-size: var(--font-size-base, 16px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}

.detail-item p {
  font-size: var(--font-size-base, 16px);
  line-height: 1.6;
  color: var(--color-text-secondary, #4a5249);
  margin: 0;
}

/* 评价区 */
.reviews-content {
  padding: var(--spacing-6, 24px);
}

.review-summary {
  display: flex;
  align-items: center;
  gap: var(--spacing-4, 16px);
  padding-bottom: var(--spacing-4, 16px);
  border-bottom: 1px solid var(--color-border, #e5e0d8);
  margin-bottom: var(--spacing-4, 16px);
}

.rating-score {
  font-size: var(--font-size-5xl, 60px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-warning, #c9a661);
}

.rating-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-1, 4px);
}

.rating-stars {
  font-size: var(--font-size-xl, 24px);
  color: var(--color-warning, #c9a661);
  letter-spacing: var(--spacing-1, 4px);
}

.rating-count {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-4, 16px);
}

.review-item {
  padding: var(--spacing-4, 16px);
  background: var(--color-bg-secondary, #f7f5f0);
  border-radius: var(--radius-base, 8px);
}

.review-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-2, 8px);
}

.reviewer {
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-primary, #1f2923);
}

.review-date {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

.review-rating {
  font-size: var(--font-size-lg, 20px);
  color: var(--color-warning, #c9a661);
  letter-spacing: var(--spacing-1, 4px);
  margin-bottom: var(--spacing-2, 8px);
}

.review-text {
  font-size: var(--font-size-base, 16px);
  line-height: 1.6;
  color: var(--color-text-secondary, #4a5249);
  margin: 0;
}

/* 404 */
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  text-align: center;
}

.not-found-icon {
  width: 80px;
  height: 80px;
  margin-bottom: var(--spacing-4, 16px);
  color: var(--color-text-tertiary, #6b7280);
  opacity: 0.5;
}

.not-found-icon svg {
  width: 100%;
  height: 100%;
}

.not-found h2 {
  font-size: var(--font-size-2xl, 32px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-4, 16px);
}

/* 响应式 */
@media (max-width: 768px) {
  .detail-main {
    grid-template-columns: 1fr;
  }

  .image-section {
    position: static;
  }

  .product-name {
    font-size: var(--font-size-2xl, 32px);
  }

  .price-value {
    font-size: var(--font-size-3xl, 38px);
  }

  .action-buttons {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .product-detail-page {
    padding: var(--spacing-6, 24px) var(--spacing-4, 16px);
  }

  .product-name {
    font-size: var(--font-size-xl, 24px);
  }
}
</style>
