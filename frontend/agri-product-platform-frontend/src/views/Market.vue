<template>
  <div class="market-page">
    <!-- 页面标题区 -->
    <div class="page-header">
      <h1 class="page-title">农产品市场</h1>
      <p class="page-subtitle">优质农产品，直接从农户到您手中</p>
    </div>

    <!-- 筛选区 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <button class="filter-tab active" @click="filterType = 'all'">全部</button>
        <button class="filter-tab" @click="filterType = 'supply'">货源</button>
        <button class="filter-tab" @click="filterType = 'demand'">需求</button>
      </div>
      <div class="filter-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索农产品..."
          :prefix-icon="'Search'"
          size="large"
          class="search-input"
        />
      </div>
    </div>

    <!-- 商品网格 -->
    <div class="products-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <div class="product-image">
          <div class="image-placeholder">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
              <polyline points="7,10 12,15 17,10"/>
              <line x1="12" y1="15" x2="12" y2="3"/>
            </svg>
          </div>
          <div class="product-tag" :class="product.type">{{ product.type === 'supply' ? '供' : '求' }}</div>
        </div>
        <div class="product-info">
          <div class="product-name">{{ product.name }}</div>
          <div class="product-origin">{{ product.origin }}</div>
          <div class="product-footer">
            <div class="product-price">¥{{ product.price }}/{{ product.unit }}</div>
            <div class="product-seller">{{ product.seller }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const filterType = ref('all')
const searchKeyword = ref('')

const products = ref([
  { id: 1, name: '有机东北大米', origin: '黑龙江·五常', price: '8.50', unit: 'kg', seller: '张农场', type: 'supply' },
  { id: 2, name: '红富士苹果', origin: '陕西·洛川', price: '12.00', unit: 'kg', seller: '李果园', type: 'supply' },
  { id: 3, name: '散养土鸡蛋', origin: '湖北·荆门', price: '2.50', unit: '个', seller: '王养殖', type: 'supply' },
  { id: 4, name: '求购新鲜蔬菜', origin: '需求量大', price: '面议', unit: '吨', seller: '赵采购', type: 'demand' },
  { id: 5, name: '优质小麦', origin: '河南·周口', price: '4.20', unit: 'kg', seller: '刘种植', type: 'supply' },
  { id: 6, name: '求购大豆', origin: '长期需求', price: '面议', unit: '吨', seller: '食品厂', type: 'demand' },
  { id: 7, name: '新鲜时蔬套餐', origin: '山东·寿光', price: '45.00', unit: '份', seller: '阳光农场', type: 'supply' },
  { id: 8, name: '有机玉米', origin: '吉林·长春', price: '3.80', unit: 'kg', seller: '丰收合作社', type: 'supply' }
])
</script>

<style scoped>
.market-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: var(--spacing-8, 32px) var(--spacing-6, 24px);
}

.page-header {
  text-align: center;
  margin-bottom: var(--spacing-8, 32px);
}

.page-title {
  font-family: var(--font-family-display, 'Noto Serif SC', serif);
  font-size: var(--font-size-4xl, 48px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
}

.page-subtitle {
  font-size: var(--font-size-lg, 20px);
  color: var(--color-text-tertiary, #6b7280);
  margin: 0;
}

/* ===== 筛选区 ===== */
.filter-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-6, 24px);
  gap: var(--spacing-4, 16px);
  flex-wrap: wrap;
}

.filter-tabs {
  display: flex;
  gap: var(--spacing-2, 8px);
}

.filter-tab {
  padding: var(--spacing-2, 8px) var(--spacing-4, 16px);
  font-size: var(--font-size-sm, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-secondary, #4a5249);
  background: transparent;
  border: 1px solid var(--color-border, #e5e0d8);
  border-radius: var(--radius-base, 8px);
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease);
}

.filter-tab:hover {
  color: var(--color-primary, #2d5a3d);
  border-color: var(--color-primary, #2d5a3d);
}

.filter-tab.active {
  color: var(--color-text-inverse, #ffffff);
  background: var(--color-primary, #2d5a3d);
  border-color: var(--color-primary, #2d5a3d);
}

.search-input {
  width: 300px;
}

/* ===== 商品网格 ===== */
.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-5, 20px);
}

.product-card {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease);
}

.product-card:hover {
  box-shadow: var(--shadow-base, 0 4px 12px rgba(31, 41, 35, 0.08));
  transform: translateY(-4px);
}

.product-image {
  position: relative;
  aspect-ratio: 1;
  background: var(--color-bg-secondary, #f7f5f0);
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-text-tertiary, #6b7280);
}

.image-placeholder svg {
  width: 48px;
  height: 48px;
}

.product-tag {
  position: absolute;
  top: var(--spacing-3, 12px);
  left: var(--spacing-3, 12px);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-xs, 12px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-text-inverse, #ffffff);
  border-radius: var(--radius-base, 8px);
}

.product-tag.supply {
  background: var(--color-success, #4a7c59);
}

.product-tag.demand {
  background: var(--color-warning, #c9a661);
}

.product-info {
  padding: var(--spacing-4, 16px);
}

.product-name {
  font-size: var(--font-size-base, 16px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin-bottom: var(--spacing-1, 4px);
}

.product-origin {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-tertiary, #6b7280);
  margin-bottom: var(--spacing-3, 12px);
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.product-price {
  font-size: var(--font-size-base, 16px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--color-error, #b85c38);
}

.product-seller {
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .search-input {
    width: 240px;
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .search-input {
    width: 100%;
  }

  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .page-title {
    font-size: var(--font-size-3xl, 38px);
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }

  .market-page {
    padding: var(--spacing-6, 24px) var(--spacing-4, 16px);
  }
}
</style>
