<template>
  <div class="knowledge-list-page">
    <!-- 页面标题区 -->
    <div class="page-header">
      <h1 class="page-title">农业知识库</h1>
      <p class="page-subtitle">专业农业知识分享，助您科学种植</p>
    </div>

    <!-- 筛选区 -->
    <div class="filter-section">
      <div class="filter-tabs">
        <button
          v-for="category in categories"
          :key="category.id"
          class="filter-tab"
          :class="{ active: selectedCategory === category.id }"
          @click="selectedCategory = category.id"
        >
          {{ category.name }}
        </button>
      </div>
      <div class="filter-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索知识文章..."
          :prefix-icon="'Search'"
          size="large"
          class="search-input"
        />
      </div>
    </div>

    <!-- 知识列表 -->
    <div class="knowledge-grid">
      <div v-for="knowledge in filteredKnowledge" :key="knowledge.id" class="knowledge-card" @click="handleViewKnowledge(knowledge)">
        <div class="knowledge-image">
          <img v-if="knowledge.image" :src="knowledge.image" :alt="knowledge.title">
          <div v-else class="image-placeholder">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
              <path d="M14 2v6h6M16 13H8M16 17H8M10 9H8"/>
            </svg>
          </div>
          <div class="knowledge-category">{{ knowledge.category }}</div>
        </div>
        <div class="knowledge-content">
          <h3 class="knowledge-title">{{ knowledge.title }}</h3>
          <p class="knowledge-excerpt">{{ knowledge.excerpt }}</p>
          <div class="knowledge-meta">
            <span class="meta-author">{{ knowledge.author }}</span>
            <span class="meta-time">{{ knowledge.time }}</span>
            <span class="meta-views">{{ knowledge.views }} 阅读</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="hasMore">
      <el-button @click="loadMore" :loading="loading">加载更多</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const selectedCategory = ref('all')
const searchKeyword = ref('')
const loading = ref(false)
const hasMore = ref(true)

const categories = [
  { id: 'all', name: '全部' },
  { id: 'grain', name: '粮食作物' },
  { id: 'vegetable', name: '蔬菜种植' },
  { id: 'fruit', name: '果树栽培' },
  { id: 'soil', name: '土壤肥料' },
  { id: 'pest', name: '病虫害防治' }
]

const knowledgeList = ref([
  {
    id: 1,
    title: '春季小麦田间管理技术要点',
    excerpt: '详细介绍春季小麦返青后的田间管理措施，包括施肥、浇水、病虫害防治等关键技术',
    category: '粮食作物',
    image: 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&h=250&fit=crop',
    author: '李建国',
    time: '2024-03-15',
    views: 1234,
    categoryId: 'grain'
  },
  {
    id: 2,
    title: '温室蔬菜病虫害绿色防控技术',
    excerpt: '介绍温室蔬菜主要病虫害的识别方法及绿色防控技术，减少农药使用，提高蔬菜品质',
    category: '蔬菜种植',
    image: 'https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=400&h=250&fit=crop',
    author: '王淑芬',
    time: '2024-03-12',
    views: 856,
    categoryId: 'vegetable'
  },
  {
    id: 3,
    title: '苹果树修剪技术详解',
    excerpt: '详细介绍苹果树的修剪原则、方法和最佳时机，帮助提高产量和果实品质',
    category: '果树栽培',
    image: 'https://images.unsplash.com/photo-1592150621744-aca64f82940c?w=400&h=250&fit=crop',
    author: '赵海燕',
    time: '2024-03-10',
    views: 2341,
    categoryId: 'fruit'
  },
  {
    id: 4,
    title: '科学施肥提高农产品品质',
    excerpt: '讲解如何根据土壤状况和作物需求科学施肥，提高肥料利用率，改善农产品品质',
    category: '土壤肥料',
    image: null,
    author: '张明远',
    time: '2024-03-08',
    views: 1567,
    categoryId: 'soil'
  },
  {
    id: 5,
    title: '水稻稻瘟病综合防治技术',
    excerpt: '介绍水稻稻瘟病的识别特征、发生规律及综合防治技术，确保水稻稳产高产',
    category: '病虫害防治',
    image: 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=400&h=250&fit=crop',
    author: '李建国',
    time: '2024-03-05',
    views: 3456,
    categoryId: 'pest'
  },
  {
    id: 6,
    title: '大棚蔬菜标准化种植指南',
    excerpt: '详细介绍大棚蔬菜的标准化种植流程，从育苗到采收的全过程管理技术',
    category: '蔬菜种植',
    image: 'https://images.unsplash.com/photo-1592419044706-39796d40798c?w=400&h=250&fit=crop',
    author: '王淑芬',
    time: '2024-03-01',
    views: 2890,
    categoryId: 'vegetable'
  }
])

const filteredKnowledge = computed(() => {
  let result = knowledgeList.value

  // 分类筛选
  if (selectedCategory.value !== 'all') {
    result = result.filter(item => item.categoryId === selectedCategory.value)
  }

  // 关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(item =>
      item.title.toLowerCase().includes(keyword) ||
      item.excerpt.toLowerCase().includes(keyword)
    )
  }

  return result
})

const handleViewKnowledge = (knowledge) => {
  if (!userStore.isLoggedIn()) {
    ElMessage.info('请先登录后查看知识详情')
    router.push('/login')
    return
  }
  // TODO: 跳转到知识详情页
  ElMessage.info('查看知识：' + knowledge.title)
}

const loadMore = () => {
  loading.value = true
  // TODO: 从后端加载更多数据
  setTimeout(() => {
    loading.value = false
    hasMore.value = false
  }, 1000)
}
</script>

<style scoped>
.knowledge-list-page {
  max-width: 1200px;
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
  flex-wrap: wrap;
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

/* ===== 知识网格 ===== */
.knowledge-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-5, 20px);
}

.knowledge-card {
  background: var(--color-bg-primary, #ffffff);
  border-radius: var(--radius-lg, 12px);
  border: 1px solid var(--color-border, #e5e0d8);
  overflow: hidden;
  cursor: pointer;
  transition: all var(--transition-fast, 150ms ease);
}

.knowledge-card:hover {
  box-shadow: var(--shadow-base, 0 4px 12px rgba(31, 41, 35, 0.08));
  transform: translateY(-4px);
}

.knowledge-image {
  position: relative;
  aspect-ratio: 16 / 10;
  background: var(--color-bg-secondary, #f7f5f0);
  overflow: hidden;
}

.knowledge-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-base, 250ms ease);
}

.knowledge-card:hover .knowledge-image img {
  transform: scale(1.05);
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

.knowledge-category {
  position: absolute;
  bottom: var(--spacing-3, 12px);
  left: var(--spacing-3, 12px);
  padding: var(--spacing-1, 4px) var(--spacing-3, 12px);
  font-size: var(--font-size-xs, 12px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--color-text-inverse, #ffffff);
  background: rgba(45, 90, 61, 0.85);
  backdrop-filter: blur(4px);
  border-radius: var(--radius-full, 9999px);
}

.knowledge-content {
  padding: var(--spacing-4, 16px);
}

.knowledge-title {
  font-size: var(--font-size-base, 16px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--color-text-primary, #1f2923);
  margin: 0 0 var(--spacing-2, 8px);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.knowledge-excerpt {
  font-size: var(--font-size-sm, 14px);
  color: var(--color-text-secondary, #4a5249);
  line-height: 1.5;
  margin: 0 0 var(--spacing-3, 12px);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.knowledge-meta {
  display: flex;
  gap: var(--spacing-3, 12px);
  font-size: var(--font-size-xs, 12px);
  color: var(--color-text-tertiary, #6b7280);
}

.meta-author {
  color: var(--color-primary, #2d5a3d);
  font-weight: var(--font-weight-medium, 500);
}

/* ===== 加载更多 ===== */
.load-more {
  text-align: center;
  margin-top: var(--spacing-8, 32px);
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .knowledge-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .search-input {
    width: 240px;
  }
}

@media (max-width: 768px) {
  .knowledge-grid {
    grid-template-columns: 1fr;
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
</style>
