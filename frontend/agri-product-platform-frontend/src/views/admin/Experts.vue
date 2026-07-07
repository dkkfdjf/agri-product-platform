<template>
  <div class="admin-experts">
    <h2>专家管理</h2>

    <el-card>
      <el-table :data="expertList" stripe>
        <el-table-column prop="userName" label="账号" width="120" />
        <el-table-column prop="expertName" label="专家姓名" width="120" />
        <el-table-column prop="title" label="职称" width="120" />
        <el-table-column prop="specialty" label="专业领域" />
        <el-table-column prop="phone" label="联系电话" width="130" />
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
          @size-change="loadExpertList"
          @current-change="loadExpertList"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑专家" width="600px">
      <el-form :model="expertForm" :rules="expertRules" ref="expertFormRef" label-width="100px">
        <el-form-item label="账号">
          <el-input v-model="expertForm.userName" disabled />
        </el-form-item>
        <el-form-item label="专家姓名" prop="expertName">
          <el-input v-model="expertForm.expertName" placeholder="请输入专家姓名" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="expertForm.title" placeholder="请选择职称" style="width: 100%">
            <el-option label="初级农艺师" value="初级农艺师" />
            <el-option label="农艺师" value="农艺师" />
            <el-option label="高级农艺师" value="高级农艺师" />
            <el-option label="研究员" value="研究员" />
            <el-option label="教授" value="教授" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业领域" prop="specialty">
          <el-input v-model="expertForm.specialty" placeholder="请输入专业领域" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="expertForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="个人简介" prop="bio">
          <el-input v-model="expertForm.bio" type="textarea" :rows="4" placeholder="请输入个人简介" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="expertForm.status">
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

const expertList = ref([])
const editVisible = ref(false)
const submitting = ref(false)
const expertFormRef = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 专家表单
const expertForm = reactive({
  userName: '',
  expertName: '',
  title: '',
  specialty: '',
  phone: '',
  bio: '',
  status: 1
})

// 表单验证规则
const expertRules = {
  expertName: [{ required: true, message: '请输入专家姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }],
  specialty: [{ required: true, message: '请输入专业领域', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  bio: [{ required: true, message: '请输入个人简介', trigger: 'blur' }]
}

// 加载专家列表
const loadExpertList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await adminApi.getExpertList({
    //   page: currentPage.value,
    //   size: pageSize.value
    // })

    // 模拟数据
    expertList.value = [
      {
        userName: 'expert001',
        expertName: '张教授',
        title: '高级农艺师',
        specialty: '水稻种植技术',
        phone: '13800138000',
        bio: '从事水稻种植研究20年',
        status: 1,
        createTime: '2024-01-10 10:00:00'
      },
      {
        userName: 'expert002',
        expertName: '李研究员',
        title: '研究员',
        specialty: '病虫害防治',
        phone: '13900139000',
        bio: '专注于农作物病虫害防治技术研究',
        status: 1,
        createTime: '2024-01-11 14:30:00'
      }
    ]
    total.value = 2
  } catch (error) {
    ElMessage.error('加载专家列表失败')
  }
}

// 编辑专家
const handleEdit = (row) => {
  Object.assign(expertForm, row)
  editVisible.value = true
}

// 切换状态
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该专家吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.updateExpert(row.userName, { status: row.status === 1 ? 0 : 1 })

      ElMessage.success(`${action}成功`)
      loadExpertList()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

// 删除专家
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该专家吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.deleteExpert(row.userName)

      ElMessage.success('删除成功')
      loadExpertList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交编辑
const handleSubmitEdit = async () => {
  const valid = await expertFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // await adminApi.updateExpert(expertForm.userName, expertForm)

    ElMessage.success('保存成功')
    editVisible.value = false
    loadExpertList()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadExpertList()
})
</script>

<style scoped>
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
