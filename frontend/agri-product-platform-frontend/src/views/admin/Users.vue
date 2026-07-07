<template>
  <div class="admin-users">
    <h2>用户管理</h2>

    <el-card>
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <el-select v-model="filter.role" placeholder="选择角色" clearable style="width: 150px">
          <el-option label="农户" value="farmer" />
          <el-option label="专家" value="expert" />
          <el-option label="银行" value="bank" />
          <el-option label="管理员" value="admin" />
        </el-select>
        <el-input v-model="filter.keyword" placeholder="搜索账号/姓名" style="width: 200px" clearable />
        <el-button type="primary" @click="loadUserList">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>

      <el-table :data="userList" stripe>
        <el-table-column prop="userName" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadUserList"
          @current-change="loadUserList"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="600px">
      <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
        <el-form-item label="账号">
          <el-input v-model="userForm.userName" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="userForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEdit" :loading="submitting">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const userList = ref([])
const editVisible = ref(false)
const submitting = ref(false)
const userFormRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 筛选条件
const filter = reactive({
  role: '',
  keyword: ''
})

// 用户表单
const userForm = reactive({
  userName: '',
  realName: '',
  phone: '',
  email: '',
  status: 1
})

// 表单验证规则
const userRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 角色类型
const getRoleType = (role) => {
  const types = {
    farmer: 'success',
    expert: 'warning',
    bank: 'danger',
    admin: 'info'
  }
  return types[role] || 'info'
}

// 角色文本
const getRoleText = (role) => {
  const texts = {
    farmer: '农户',
    expert: '专家',
    bank: '银行',
    admin: '管理员'
  }
  return texts[role] || '未知'
}

// 加载用户列表
const loadUserList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await adminApi.getUserList({
    //   page: currentPage.value,
    //   size: pageSize.value,
    //   ...filter
    // })

    // 模拟数据
    userList.value = [
      {
        userName: 'farmer001',
        realName: '张三',
        role: 'farmer',
        phone: '13800138000',
        email: 'zhangsan@example.com',
        status: 1,
        createTime: '2024-01-10 10:00:00'
      },
      {
        userName: 'expert001',
        realName: '李教授',
        role: 'expert',
        phone: '13900139000',
        email: 'liprof@example.com',
        status: 1,
        createTime: '2024-01-11 14:30:00'
      },
      {
        userName: 'bank001',
        realName: '农业银行',
        role: 'bank',
        phone: '400-12345678',
        email: 'abc@example.com',
        status: 1,
        createTime: '2024-01-12 09:00:00'
      }
    ]
    total.value = 3
  } catch (error) {
    ElMessage.error('加载用户列表失败')
  }
}

// 编辑用户
const handleEdit = (row) => {
  Object.assign(userForm, row)
  editVisible.value = true
}

// 切换状态
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.updateUser(row.userName, { status: row.status === 1 ? 0 : 1 })

      ElMessage.success(`${action}成功`)
      loadUserList()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.deleteUser(row.userName)

      ElMessage.success('删除成功')
      loadUserList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交编辑
const handleSubmitEdit = async () => {
  const valid = await userFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await adminApi.updateUser(userForm.userName, userForm)

    ElMessage.success('保存成功')
    editVisible.value = false
    loadUserList()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
