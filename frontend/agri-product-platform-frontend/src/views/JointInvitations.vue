<template>
  <div class="joint-invitations-page">
    <div class="container">
      <BackBar to="/" />
      <div class="page-header">
        <h1 class="page-title">我的联合贷款邀请</h1>
        <p class="page-subtitle">您被邀请作为他人融资申请的联合贷款人，请确认是否同意</p>
      </div>

      <div v-loading="loading" class="list">
        <div v-for="inv in invitations" :key="inv.id" class="invite-card">
          <div class="invite-top">
            <div class="who">
              <span class="k">申请人</span>
              <span class="name">{{ inv.applicantRealName || inv.applicantUserName }}</span>
            </div>
            <el-tag :type="statusType(inv.status)" size="small">{{ statusText(inv.status) }}</el-tag>
          </div>

          <div class="invite-body">
            <div class="row"><span class="k">融资产品</span>{{ inv.productName || '—' }}</div>
            <div class="row"><span class="k">融资金额</span>¥ {{ formatNum(inv.amount) }}</div>
            <div class="row"><span class="k">邀请时间</span>{{ formatTime(inv.createTime) }}</div>
          </div>

          <div class="invite-actions" v-if="inv.status === 0">
            <el-button type="primary" :loading="actingId === inv.id" @click="accept(inv)">同意</el-button>
            <el-button type="danger" plain :loading="actingId === inv.id" @click="decline(inv)">拒绝</el-button>
          </div>
          <div class="invite-handled" v-else-if="inv.handleTime">
            处理时间：{{ formatTime(inv.handleTime) }}
          </div>
        </div>

        <el-empty v-if="!loading && invitations.length === 0" description="暂无联合贷款邀请" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import BackBar from '@/components/BackBar.vue'
import {
  getMyJointInvitations,
  acceptJointInvitation,
  declineJointInvitation
} from '@/api/finance'

const loading = ref(false)
const invitations = ref([])
const actingId = ref(null)

const load = async () => {
  loading.value = true
  try {
    const res = await getMyJointInvitations()
    invitations.value = res.data || []
  } catch (e) {
    ElMessage.error('加载邀请失败')
  } finally {
    loading.value = false
  }
}

const accept = async (inv) => {
  try {
    await ElMessageBox.confirm(
      `确认作为 ${inv.applicantRealName || inv.applicantUserName} 的联合贷款人？同意后您的姓名/电话/身份证号将绑定到该融资申请。`,
      '同意邀请',
      { type: 'warning' }
    )
    actingId.value = inv.id
    await acceptJointInvitation(inv.id)
    ElMessage.success('已同意')
    await load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.response?.data?.message || e?.message || '操作失败')
  } finally {
    actingId.value = null
  }
}

const decline = async (inv) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该联合贷款邀请吗？', '拒绝邀请', { type: 'warning' })
    actingId.value = inv.id
    await declineJointInvitation(inv.id)
    ElMessage.success('已拒绝')
    await load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e?.response?.data?.message || e?.message || '操作失败')
  } finally {
    actingId.value = null
  }
}

const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const statusText = (s) => ({ 0: '待处理', 1: '已同意', 2: '已拒绝' }[s] || '未知')

const formatNum = (n) => {
  if (n === null || n === undefined || n === '') return '0'
  return Number(n).toLocaleString()
}

const formatTime = (t) => {
  if (!t) return '—'
  const d = new Date(t)
  if (isNaN(d.getTime())) return t
  const p = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}`
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.joint-invitations-page {
  min-height: 100vh;
  background: #f5f3ef;
  padding: 24px 0;
}

.container {
  max-width: 860px;
  margin: 0 auto;
  padding: 0 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2923;
  margin: 12px 0 8px;
}

.page-subtitle {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
}

.list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.invite-card {
  background: #fff;
  border: 1px solid #e5e0d8;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(31, 41, 35, 0.06);
}

.invite-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.who .k {
  color: #6b7280;
  font-size: 13px;
  margin-right: 8px;
}

.who .name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2923;
}

.invite-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px 16px;
  background: #faf8f4;
  border-radius: 8px;
  margin-bottom: 14px;
}

.row {
  font-size: 14px;
  color: #4a5249;
}

.row .k {
  display: inline-block;
  width: 84px;
  color: #6b7280;
}

.invite-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.invite-handled {
  text-align: right;
  font-size: 13px;
  color: #909399;
}
</style>
