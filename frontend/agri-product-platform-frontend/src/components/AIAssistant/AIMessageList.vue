<template>
  <div class="ai-message-list">
    <!-- 欢迎消息（无消息时） -->
    <div v-if="!messages || messages.length === 0" class="welcome-message">
      <el-icon class="welcome-icon"><ChatDotRound /></el-icon>
      <h3>欢迎来到智能助手</h3>
      <p>我可以帮您解答农产品平台相关问题</p>
      <div class="suggested-questions">
        <el-button
          v-for="(question, index) in suggestedQuestions"
          :key="index"
          size="small"
          @click="handleSendMessage(question)"
        >
          {{ question }}
        </el-button>
      </div>
    </div>

    <!-- 消息列表 -->
    <transition-group
      v-else
      name="message-fade"
      tag="div"
      class="messages-container"
    >
      <AIMessage
        v-for="message in messages"
        :key="message.id"
        :message="message"
        class="message-item"
      />
    </transition-group>

    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <AILoading />
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      <el-icon class="error-icon"><WarningFilled /></el-icon>
      <span>{{ error }}</span>
      <el-button size="small" type="primary" @click="handleRetry">
        重试
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ChatDotRound, WarningFilled } from '@element-plus/icons-vue'
import AIMessage from './AIMessage.vue'
import AILoading from './AILoading.vue'
import { useUserStore } from '@/stores/user'

// Props
const props = defineProps({
  messages: {
    type: Array,
    default: () => []
  },
  isLoading: {
    type: Boolean,
    default: false
  },
  error: {
    type: String,
    default: null
  }
})

// Events
const emit = defineEmits(['send-message', 'retry'])

// 建议问题列表 - 根据用户角色和当前页面动态生成
const route = useRoute()
const userStore = useUserStore()

// 基础建议问题
const baseQuestions = [
  '平台有哪些功能？',
  '如何联系客服？'
]

// 根据角色定制的建议问题
const roleQuestions = {
  farmer: [
    '如何发布农产品？',
    '如何申请融资？',
    '融资流程是怎样的？',
    '怎样查看我的订单？'
  ],
  buyer: [
    '如何购买农产品？',
    '如何使用购物车？',
    '支付方式有哪些？',
    '怎样查看订单状态？'
  ],
  expert: [
    '如何发布知识文章？',
    '如何回答用户问题？',
    '怎样查看预约记录？',
    '如何设置专家专长？'
  ],
  bank: [
    '如何审批融资申请？',
    '怎样查看贷款产品？',
    '如何管理还款记录？',
    '融资匹配规则是什么？'
  ],
  admin: [
    '如何管理用户？',
    '如何审核专家申请？',
    '怎样管理系统配置？',
    '如何管理轮播图？'
  ]
}

// 根据当前页面定制的建议问题
const pageQuestions = {
  'FarmerProducts': ['如何发布新产品？', '如何编辑商品信息？', '怎样设置商品价格？'],
  'FarmerFinance': ['如何申请贷款？', '贷款利率是多少？', '需要什么材料？'],
  'FarmerOrders': ['如何处理订单？', '发货流程是什么？', '如何查看物流？'],
  'BuyerCart': ['如何修改购物车？', '结算流程是什么？', '如何使用优惠券？'],
  'BuyerCheckout': ['如何添加收货地址？', '支付方式有哪些？', '可以使用积分吗？'],
  'ExpertKnowledge': ['如何发布新文章？', '文章格式要求是什么？', '如何插入图片？'],
  'ExpertQuestions': ['如何回答问题？', '回答规范是什么？', '如何获得更多积分？'],
  'BankApprovals': ['审批标准是什么？', '如何查看农户信用？', '如何拒绝申请？'],
  'BankMatching': ['如何进行融资匹配？', '匹配规则是什么？', '如何调整利率？'],
  'AdminUser': ['如何禁用用户？', '如何修改用户角色？', '用户数据如何导出？']
}

const suggestedQuestions = ref([])

// 根据当前路由和角色生成建议问题
const generateSuggestedQuestions = () => {
  const questions = [...baseQuestions]

  // 添加角色相关建议
  const userRole = userStore.role
  if (userRole && roleQuestions[userRole]) {
    questions.push(...roleQuestions[userRole].slice(0, 2))
  }

  // 添加页面相关建议
  const currentRouteName = route.name
  if (currentRouteName && pageQuestions[currentRouteName]) {
    questions.push(...pageQuestions[currentRouteName].slice(0, 2))
  }

  // 限制建议问题数量
  return questions.slice(0, 4)
}

// 监听路由变化，更新建议问题
watch(() => route.name, () => {
  suggestedQuestions.value = generateSuggestedQuestions()
}, { immediate: true })

// 监听用户角色变化，更新建议问题
watch(() => userStore.role, () => {
  suggestedQuestions.value = generateSuggestedQuestions()
}, { immediate: true })

// 处理发送建议问题
const handleSendMessage = (question) => {
  emit('send-message', question)
}

// 处理重试
const handleRetry = () => {
  emit('retry')
}

// 自动滚动到底部
const scroll_to_bottom = () => {
  nextTick(() => {
    const container = document.querySelector('.messages-container')
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}

// 监听消息变化，自动滚动
watch(() => props.messages, () => {
  scroll_to_bottom()
}, { deep: true })

// 监听加载状态变化，自动滚动
watch(() => props.isLoading, () => {
  scroll_to_bottom()
})
</script>

<style scoped>
.ai-message-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  position: relative;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
  text-align: center;
}

.welcome-icon {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 20px;
}

.welcome-message h3 {
  font-size: 24px;
  margin: 0 0 10px 0;
  color: var(--color-text-primary, #303133);
  transition: color var(--transition-base, 250ms ease);
}

.welcome-message p {
  font-size: 14px;
  color: var(--color-text-secondary, #606266);
  margin: 0 0 30px 0;
  transition: color var(--transition-base, 250ms ease);
}

.suggested-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  max-width: 500px;
}

/* 消息容器 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  scroll-behavior: smooth;
}

.message-item {
  margin-bottom: 16px;
}

/* 消息动画 */
.message-fade-enter-active,
.message-fade-leave-active {
  transition: all 0.3s ease;
}

.message-fade-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.message-fade-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* 加载容器 */
.loading-container {
  padding: 20px;
  display: flex;
  justify-content: center;
}

/* 错误消息 */
.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  background-color: var(--color-error-light, #fef0f0);
  border: 1px solid var(--color-error, #fde2e2);
  border-radius: 4px;
  margin: 10px 20px;
  color: var(--color-error, #f56c6c);
  transition: background-color var(--transition-base, 250ms ease);
}

.error-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.error-message span {
  flex: 1;
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-message h3 {
    font-size: 20px;
  }

  .welcome-icon {
    font-size: 48px;
  }

  .suggested-questions {
    flex-direction: column;
    align-items: stretch;
  }

  .messages-container {
    padding: 15px;
  }

  .message-item {
    margin-bottom: 12px;
  }
}
</style>
