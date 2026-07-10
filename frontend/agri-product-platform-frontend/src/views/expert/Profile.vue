<template>
  <div class="expert-profile-container">
    <div class="page-header">
      <h2>专家个人中心</h2>
      <el-button type="primary" @click="editMode = !editMode" :icon="editMode ? Close : Edit">
        {{ editMode ? '取消编辑' : '编辑资料' }}
      </el-button>
    </div>

    <!-- 专家信息卡片 -->
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="专家姓名">
          <span v-if="!editMode">{{ expertInfo.realName }}</span>
          <el-input v-else v-model="form.realName" placeholder="请输入姓名" />
        </el-descriptions-item>

        <el-descriptions-item label="联系电话">
          <span v-if="!editMode">{{ expertInfo.phone }}</span>
          <el-input v-else v-model="form.phone" placeholder="请输入电话" />
        </el-descriptions-item>

        <el-descriptions-item label="邮箱">
          <span v-if="!editMode">{{ expertInfo.email || '未设置' }}</span>
          <el-input v-else v-model="form.email" placeholder="请输入邮箱" />
        </el-descriptions-item>

        <el-descriptions-item label="专业领域">
          <span v-if="!editMode">{{ expertInfo.specialty || '未设置' }}</span>
          <el-select
            v-else
            v-model="form.specialty"
            placeholder="请选择专业领域"
            style="width: 100%"
          >
            <el-option label="种植技术" value="种植技术" />
            <el-option label="养殖技术" value="养殖技术" />
            <el-option label="病虫害防治" value="病虫害防治" />
            <el-option label="农产品加工" value="农产品加工" />
            <el-option label="农业机械" value="农业机械" />
            <el-option label="农业经济" value="农业经济" />
            <el-option label="农业政策" value="农业政策" />
          </el-select>
        </el-descriptions-item>

        <el-descriptions-item label="工作年限">
          <span v-if="!editMode">{{ expertInfo.workYears ? `${expertInfo.workYears}年` : '未设置' }}</span>
          <el-input-number v-else v-model="form.workYears" :min="1" :max="50" />
        </el-descriptions-item>

        <el-descriptions-item label="咨询费用">
          <span v-if="!editMode">¥{{ expertInfo.consultFee || 0 }}/次</span>
          <el-input-number v-else v-model="form.consultFee" :min="0" :max="10000" :step="10" />
        </el-descriptions-item>

        <el-descriptions-item label="个人简介" :span="2">
          <span v-if="!editMode">{{ expertInfo.bio || '暂无简介' }}</span>
          <el-input
            v-else
            v-model="form.bio"
            type="textarea"
            :rows="4"
            placeholder="请输入个人简介"
          />
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

      <el-tabs v-model="activeStatus" @tab-change="loadReservations">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待确认" name="pending" />
        <el-tab-pane label="已确认" name="confirmed" />
        <el-tab-pane label="已完成" name="completed" />
        <el-tab-pane label="已拒绝" name="rejected" />
      </el-tabs>

      <el-table :data="reservations" v-loading="loading">
        <el-table-column prop="userName" label="用户姓名" width="120" />
        <el-table-column prop="userPhone" label="联系电话" width="130" />
        <el-table-column prop="reserveDate" label="预约日期" width="120" />
        <el-table-column prop="reserveTime" label="预约时间" width="100" />
        <el-table-column prop="question" label="咨询问题" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              size="small"
              @click="updateStatus(row, 'confirmed')"
            >
              接受
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              size="small"
              @click="updateStatus(row, 'rejected')"
            >
              拒绝
            </el-button>
            <el-button
              v-if="row.status === 'confirmed'"
              type="primary"
              size="small"
              @click="updateStatus(row, 'completed')"
            >
              标记完成
            </el-button>
            <el-button type="info" size="small" @click="viewDetail(row)">
              查看详情
            </el-button>
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
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="500px">
      <div v-if="currentReservation">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户姓名">
            {{ currentReservation.userName }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ currentReservation.userPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="预约日期">
            {{ currentReservation.reserveDate }}
          </el-descriptions-item>
          <el-descriptions-item label="预约时间">
            {{ currentReservation.reserveTime }}
          </el-descriptions-item>
          <el-descriptions-item label="咨询问题">
            {{ currentReservation.question }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentReservation.status)">
              {{ getStatusText(currentReservation.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间" v-if="currentReservation.createTime">
            {{ currentReservation.createTime }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Close } from '@element-plus/icons-vue'
import { getExpertInfo, updateExpertInfo, getExpertReserveList, updateReserveStatus } from '@/api/expert'

const editMode = ref(false)
const saving = ref(false)
const loading = ref(false)
const detailDialogVisible = ref(false)
const currentReservation = ref(null)

const activeStatus = ref('all')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const expertInfo = ref({})
const reservations = ref([])

const form = reactive({
  realName: '',
  phone: '',
  email: '',
  specialty: '',
  workYears: 0,
  consultFee: 0,
  bio: ''
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

const saveProfile = async () => {
  try {
    saving.value = true
    await updateExpertInfo(form)
    ElMessage.success('保存成功')
    expertInfo.value = { ...form }
    editMode.value = false
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const cancelEdit = () => {
  Object.assign(form, expertInfo.value)
  editMode.value = false
}

const loadReservations = async () => {
  try {
    loading.value = true
    const params = {
      page: page.value,
      pageSize: pageSize.value
    }
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }
    const res = await getExpertReserveList(params)
    reservations.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载预约列表失败')
  } finally {
    loading.value = false
  }
}

const updateStatus = async (row, status) => {
  try {
    await updateReserveStatus(row.reserveId, status)
    ElMessage.success('操作成功')
    await loadReservations()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const viewDetail = (row) => {
  currentReservation.value = row
  detailDialogVisible.value = true
}

const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    confirmed: 'success',
    completed: 'info',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    rejected: '已拒绝'
  }
  return map[status] || status
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
