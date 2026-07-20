<template>
  <div class="profile-container">
    <h1>个人中心</h1>

    <el-card class="user-card">
      <div class="user-info">
        <el-avatar :size="80" :src="userStore.userAvatar() || '/images/default-avatar.png'" />
        <div class="info">
          <h2>{{ userStore.displayName() }}</h2>
          <p class="role">{{ getRoleText(userStore.role) }}</p>
          <p class="phone">{{ userStore.userInfo?.phone || '未设置电话' }}</p>
          <p class="email">{{ userStore.userInfo?.email || '未设置邮箱' }}</p>
        </div>
        <el-button type="primary" @click="goToEdit" class="edit-btn">编辑资料</el-button>
      </div>
    </el-card>

    <h3 class="section-title">快捷功能</h3>
    <el-row :gutter="20" class="menu-row">
      <el-col :span="8">
        <el-card class="menu-card" @click="$router.push('/order')">
          <div class="menu-item">
            <el-icon :size="32" color="#409eff"><Document /></el-icon>
            <span>我的订单</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="menu-card" @click="$router.push('/cart')">
          <div class="menu-item">
            <el-icon :size="32" color="#67c23a"><ShoppingCart /></el-icon>
            <span>购物车</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="menu-card" @click="$router.push('/address')">
          <div class="menu-item">
            <el-icon :size="32" color="#e6a23c"><Location /></el-icon>
            <span>地址管理</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="menu-row" v-if="isFarmer || isBuyer">
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/expert-help')">
          <div class="menu-item">
            <el-icon :size="32" color="#909399"><ChatLineSquare /></el-icon>
            <span>专家咨询</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/knowledge')">
          <div class="menu-item">
            <el-icon :size="32" color="#f56c6c"><Reading /></el-icon>
            <span>农业知识</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="menu-row" v-if="isFarmer || isBuyer">
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/role-application')">
          <div class="menu-item">
            <el-icon :size="32" color="#b85c38"><Promotion /></el-icon>
            <span>角色升级申请</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="menu-row" v-if="isFarmer">
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/farmer/my-finance')">
          <div class="menu-item">
            <el-icon :size="32" color="#409eff"><Money /></el-icon>
            <span>我的融资</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/product/publish')">
          <div class="menu-item">
            <el-icon :size="32" color="#67c23a"><Plus /></el-icon>
            <span>发布商品</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="menu-row" v-if="isExpert">
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/knowledge/publish')">
          <div class="menu-item">
            <el-icon :size="32" color="#409eff"><Edit /></el-icon>
            <span>发布知识</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/expert/reservations')">
          <div class="menu-item">
            <el-icon :size="32" color="#e6a23c"><Calendar /></el-icon>
            <span>预约管理</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="menu-row" v-if="isBank">
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/bank/approvals')">
          <div class="menu-item">
            <el-icon :size="32" color="#409eff"><Briefcase /></el-icon>
            <span>融资审批</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/bank/products')">
          <div class="menu-item">
            <el-icon :size="32" color="#67c23a"><Coin /></el-icon>
            <span>融资产品</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="menu-row" v-if="isAdmin">
      <el-col :span="12">
        <el-card class="menu-card" @click="$router.push('/admin/dashboard')">
          <div class="menu-item">
            <el-icon :size="32" color="#909399"><DataAnalysis /></el-icon>
            <span>管理后台</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Document,
  ShoppingCart,
  Location,
  Money,
  Plus,
  Edit,
  Calendar,
  Briefcase,
  ChatLineSquare,
  Reading,
  DataAnalysis,
  Promotion
} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const isFarmer = computed(() => userStore.role === 'farmer')
const isBuyer = computed(() => userStore.role === 'buyer')
const isExpert = computed(() => userStore.role === 'expert')
const isBank = computed(() => userStore.role === 'bank')
const isAdmin = computed(() => userStore.role === 'admin')

const getRoleText = (role) => {
  const map = {
    farmer: '农户',
    buyer: '买家',
    expert: '专家',
    bank: '银行',
    admin: '管理员'
  }
  return map[role] || role
}

const goToEdit = () => {
  router.push('/profile/edit')
}
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.profile-container h1 {
  margin-bottom: 20px;
  color: #303133;
}

.user-card {
  margin-bottom: 30px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
}

.info h2 {
  margin: 0 0 10px;
  color: #303133;
}

.info .role {
  margin: 5px 0;
  color: #409eff;
  font-weight: 500;
}

.info .phone,
.info .email {
  margin: 5px 0;
  color: #909399;
  font-size: 14px;
}

.edit-btn {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.section-title {
  margin-bottom: 20px;
  color: #606266;
}

.menu-row {
  margin-bottom: 20px;
}

.menu-card {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.menu-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px;
}

.menu-item span {
  font-size: 16px;
  color: #606266;
}
</style>
