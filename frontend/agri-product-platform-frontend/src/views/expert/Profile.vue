<template>
  <div class="expert-profile-container">
    <div class="page-header">
      <h2>专家个人中心</h2>
      <el-button type="primary" @click="toggleEdit" :icon="editMode ? Close : Edit">
        {{ editMode ? '取消编辑' : '编辑资料' }}
      </el-button>
    </div>

    <!-- 专家信息卡片 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header"><span>基本信息</span></div>
      </template>

      <div class="avatar-row">
        <el-avatar :size="84" :src="avatarUrl" class="expert-avatar">
          {{ avatarFallback }}
        </el-avatar>
        <div class="avatar-meta">
          <el-upload
            action="/api/upload"
            :show-file-list="false"
            accept="image/*"
            :before-upload="beforeAvatarUpload"
            :on-success="onAvatarSuccess"
            :on-error="onAvatarError"
          >
            <el-button :loading="uploading" :icon="Camera">修改头像</el-button>
          </el-upload>
          <div class="avatar-tip">支持 JPG / PNG，建议正方形，≤5MB</div>
        </div>
      </div>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="专家用户名">
          {{ expertInfo.userName || '—' }}
        </el-descriptions-item>

        <el-descriptions-item label="专家姓名">
          <span v-if="!editMode">{{ expertInfo.realName || '—' }}</span>
          <el-input v-else v-model="form.realName" placeholder="请输入姓名" />
        </el-descriptions-item>

        <el-descriptions-item label="联系电话">
          <span v-if="!editMode">{{ expertInfo.phone || '—' }}</span>
          <el-input v-else v-model="form.phone" placeholder="请输入电话" />
        </el-descriptions-item>

        <el-descriptions-item label="专业领域">
          <span v-if="!editMode">{{ expertInfo.profession || '—' }}</span>
          <el-input v-else v-model="form.profession" placeholder="如：农学、植保、土壤肥料" />
        </el-descriptions-item>

        <el-descriptions-item label="职位">
          <span v-if="!editMode">{{ expertInfo.position || '—' }}</span>
          <el-input v-else v-model="form.position" placeholder="如：教授、研究员" />
        </el-descriptions-item>

        <el-descriptions-item label="所属单位">
          <span v-if="!editMode">{{ expertInfo.belong || '—' }}</span>
          <el-input v-else v-model="form.belong" placeholder="如：山东省农科院" />
        </el-descriptions-item>
      </el-descriptions>

      <div class="save-actions" v-if="editMode">
        <el-button @click="cancelEdit">取消</el-button>
        <el-button type="primary" @click="saveProfile" :loading="saving">保存修改</el-button>
      </div>
    </el-card>

    <!-- 预约管理 -->
    <el-card class="reservations-card">
      <template #header>
        <div class="card-header">
          <span>预约管理</span>
          <el-tag type="info">共 {{ total }} 条预约</el-tag>
        </div>
      </template>

      <el-radio-group v-model="filterStatus" @change="loadReservations" class="filter-bar">
        <el-radio-button :value="-1">全部</el-radio-button>
        <el-radio-button :value="0">待处理</el-radio-button>
        <el-radio-button :value="1">已完成</el-radio-button>
        <el-radio-button :value="2">已拒绝</el-radio-button>
        <el-radio-button :value="3">已过期</el-radio-button>
      </el-radio-group>

      <el-table :data="reservations" v-loading="loading" style="margin-top: 16px">
        <el-table-column prop="questioner" label="预约人" width="110" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="plantName" label="农作物" width="100" />
        <el-table-column prop="area" label="面积" width="90" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="success" size="small" @click="handleStatus(row, 1)">接受</el-button>
            <el-button v-if="row.status === 0" type="danger" size="small" @click="handleStatus(row, 2)">拒绝</el-button>
            <el-button type="info" size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadReservations"
          @current-change="loadReservations"
        />
      </div>
    </el-card>

    <!-- 预约详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="560px">
      <el-descriptions :column="1" border v-if="currentReservation">
        <el-descriptions-item label="预约人">{{ currentReservation.questioner }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentReservation.phone }}</el-descriptions-item>
        <el-descriptions-item label="农作物">{{ currentReservation.plantName }}</el-descriptions-item>
        <el-descriptions-item label="面积">{{ currentReservation.area }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentReservation.address }}</el-descriptions-item>
        <el-descriptions-item label="土壤条件">{{ currentReservation.soilCondition }}</el-descriptions-item>
        <el-descriptions-item label="作物条件">{{ currentReservation.plantCondition }}</el-descriptions-item>
        <el-descriptions-item label="作物详情">{{ currentReservation.plantDetail }}</el-descriptions-item>
        <el-descriptions-item label="留言" v-if="currentReservation.message">{{ currentReservation.message }}</el-descriptions-item>
        <el-descriptions-item label="专家回复" v-if="currentReservation.answer">{{ currentReservation.answer }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentReservation.status)">{{ getStatusText(currentReservation.status) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Close, Camera } from '@element-plus/icons-vue'
import { getExpertInfo, updateExpertInfo, getExpertReserveList, updateReserveStatus } from '@/api/expert'
import { updateUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const editMode = ref(false)
const saving = ref(false)
const loading = ref(false)
const uploading = ref(false)
const detailDialogVisible = ref(false)
const currentReservation = ref(null)

// 头像：来源 tb_user.avatar（由 /user/info 返回）。专家头像复用用户表头像字段，无需新表。
const avatarUrl = ref(userStore.userInfo?.avatar || '')
const avatarFallback = computed(() => {
  const name = expertInfo.value?.realName || userStore.userInfo?.realName || userStore.userInfo?.userName || '专'
  return name.charAt(0)
})

const filterStatus = ref(-1)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const expertInfo = ref({})
const reservations = ref([])

const form = reactive({
  realName: '',
  phone: '',
  profession: '',
  position: '',
  belong: ''
})

onMounted(() => {
  loadExpertInfo()
  loadReservations()
})

const loadExpertInfo = async () => {
  try {
    const res = await getExpertInfo()
    if (res.data) {
      expertInfo.value = res.data
      Object.assign(form, res.data)
    }
  } catch (error) {
    ElMessage.error('加载专家信息失败')
  }
}

const toggleEdit = () => {
  editMode.value = !editMode.value
  if (editMode.value) {
    Object.assign(form, expertInfo.value)
  }
}

const cancelEdit = () => {
  Object.assign(form, expertInfo.value)
  editMode.value = false
}

const saveProfile = async () => {
  try {
    saving.value = true
    await updateExpertInfo(form)
    ElMessage.success('保存成功')
    expertInfo.value = { ...expertInfo.value, ...form }
    editMode.value = false
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 头像上传前校验：仅图片、≤5MB
const beforeAvatarUpload = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('仅支持上传图片')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  uploading.value = true
  return true
}

// 上传成功：把 url 写回 tb_user.avatar，并同步本地与全局 userInfo
const onAvatarSuccess = async (res) => {
  uploading.value = false
  const url = res?.data?.url
  if (!url) {
    ElMessage.error('上传失败，未获取到图片地址')
    return
  }
  try {
    await updateUserInfo({ avatar: url })
    avatarUrl.value = url
    // 同步到全局用户信息，使侧边栏/首页头像即时刷新
    if (userStore.userInfo) {
      userStore.setUserInfo({ ...userStore.userInfo, avatar: url })
    }
    ElMessage.success('头像更新成功')
  } catch (error) {
    ElMessage.error(error?.message || '保存头像失败')
  }
}

const onAvatarError = () => {
  uploading.value = false
  ElMessage.error('头像上传失败，请重试')
}

const loadReservations = async () => {
  try {
    loading.value = true
    const params = { page: page.value, pageSize: pageSize.value }
    if (filterStatus.value !== -1) {
      params.status = filterStatus.value
    }
    const res = await getExpertReserveList(params)
    reservations.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

const handleStatus = async (row, status) => {
  const action = status === 1 ? '接受' : '拒绝'
  try {
    await ElMessageBox.confirm(`确定要${action}这个预约吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updateReserveStatus(row.id, status)
    ElMessage.success(`${action}成功`)
    await loadReservations()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const viewDetail = (row) => {
  currentReservation.value = row
  detailDialogVisible.value = true
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { 0: '待处理', 1: '已完成', 2: '已拒绝', 3: '已过期' }
  return map[status] || '未知'
}
</script>

<style scoped>
.expert-profile-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.profile-card {
  margin-bottom: 20px;
}

.avatar-row {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 8px 0;
}

.expert-avatar {
  flex-shrink: 0;
  background: #2d5a3d;
  color: #fff;
  font-size: 30px;
  font-weight: 600;
}

.avatar-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.avatar-tip {
  font-size: 12px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.save-actions {
  margin-top: 20px;
  text-align: right;
}

.reservations-card {
  margin-top: 20px;
}

.filter-bar {
  margin-bottom: 4px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
