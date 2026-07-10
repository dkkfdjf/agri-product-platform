<template>
  <div class="admin-user">
    <div class="header">
      <h2>用户管理</h2>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户"
        style="width: 250px"
        clearable
        @clear="loadUsers"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-table :data="users" style="width: 100%" v-loading="loading">
      <el-table-column prop="userName" label="用户名" width="150" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="getRoleType(row.role)">
            {{ getRoleLabel(row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            link
            :type="row.status === 1 ? 'danger' : 'success'"
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="detailVisible" title="用户详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ currentUser.userName }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentUser.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleType(currentUser.role)">
            {{ getRoleLabel(currentUser.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUsers, updateUserStatus } from '@/api/admin'

const loading = ref(false)
const users = ref([])
const searchKeyword = ref('')
const detailVisible = ref(false)
const currentUser = reactive({
  userName: '',
  realName: '',
  phone: '',
  role: '',
  status: 1,
  createTime: ''
})

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUsers({ keyword: searchKeyword.value })
    users.value = res.data || []
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const getRoleType = (role) => {
  const types = {
    admin: 'danger',
    farmer: 'success',
    buyer: 'primary',
    expert: 'warning',
    bank: 'info'
  }
  return types[role] || ''
}

const getRoleLabel = (role) => {
  const labels = {
    admin: '管理员',
    farmer: '农户',
    buyer: '买家',
    expert: '专家',
    bank: '银行'
  }
  return labels[role] || role
}

const toggleStatus = async (user) => {
  try {
    const newStatus = user.status === 1 ? 0 : 1
    const action = newStatus === 1 ? '启用' : '禁用'
    await ElMessageBox.confirm(`确定${action}用户 ${user.userName} 吗？`, '提示', { type: 'warning' })
    await updateUserStatus(user.userName, { status: newStatus })
    ElMessage.success(`${action}成功`)
    await loadUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const viewDetail = (user) => {
  Object.assign(currentUser, user)
  detailVisible.value = true
}
</script>

<style scoped>
.admin-user {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}
</style>
