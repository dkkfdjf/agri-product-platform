<template>
  <div class="admin-banners">
    <div class="page-header">
      <h2>轮播图管理</h2>
      <el-button type="primary" @click="openBannerDialog">
        <el-icon><Plus /></el-icon>
        添加轮播图
      </el-button>
    </div>

    <el-card>
      <el-table :data="bannerList" stripe>
        <el-table-column prop="bannerId" label="ID" width="80" />
        <el-table-column label="图片" width="200">
          <template #default="{ row }">
            <el-image
              :src="row.imageUrl"
              fit="cover"
              style="width: 150px; height: 80px; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="link" label="跳转链接" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
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
    </el-card>

    <!-- 轮播图对话框 -->
    <el-dialog
      v-model="bannerDialogVisible"
      :title="isEdit ? '编辑轮播图' : '添加轮播图'"
      width="600px"
    >
      <el-form :model="bannerForm" :rules="bannerRules" ref="bannerFormRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="bannerForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            list-type="picture-card"
            :limit="1"
            :auto-upload="false"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="跳转链接" prop="link">
          <el-input v-model="bannerForm.link" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="bannerForm.sort" :min="0" />
          <span style="margin-left: 10px; color: #999">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="bannerForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bannerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitBanner" :loading="submitting">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const bannerList = ref([])
const bannerDialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const bannerFormRef = ref(null)
const fileList = ref([])

// 轮播图表单
const bannerForm = reactive({
  bannerId: null,
  title: '',
  imageUrl: '',
  link: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const bannerRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请上传图片', trigger: 'change' }],
  link: [{ required: true, message: '请输入跳转链接', trigger: 'blur' }]
}

// 加载轮播图列表
const loadBannerList = async () => {
  try {
    // TODO: 调用后端接口
    // const res = await homeApi.bannerList()

    // 模拟数据
    bannerList.value = [
      {
        bannerId: 1,
        title: '欢迎来到农产品融销一体平台',
        imageUrl: 'https://via.placeholder.com/800x400?text=Banner1',
        link: '/market',
        sort: 1,
        status: 1,
        createTime: '2024-01-10 10:00:00'
      },
      {
        bannerId: 2,
        title: '融资服务助力农业生产',
        imageUrl: 'https://via.placeholder.com/800x400?text=Banner2',
        link: '/finance-products',
        sort: 2,
        status: 1,
        createTime: '2024-01-11 14:30:00'
      },
      {
        bannerId: 3,
        title: '专家在线解答农业生产问题',
        imageUrl: 'https://via.placeholder.com/800x400?text=Banner3',
        link: '/expert-help',
        sort: 3,
        status: 1,
        createTime: '2024-01-12 09:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载轮播图列表失败')
  }
}

// 打开轮播图对话框
const openBannerDialog = () => {
  isEdit.value = false
  resetBannerForm()
  bannerDialogVisible.value = true
}

// 编辑轮播图
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(bannerForm, row)
  if (row.imageUrl) {
    fileList.value = [{ name: 'image', url: row.imageUrl }]
  }
  bannerDialogVisible.value = true
}

// 切换状态
const handleToggleStatus = (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该轮播图吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.updateBanner(row.bannerId, { status: row.status === 1 ? 0 : 1 })

      ElMessage.success(`${action}成功`)
      loadBannerList()
    } catch (error) {
      ElMessage.error(`${action}失败`)
    }
  })
}

// 删除轮播图
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用后端接口
      // await adminApi.deleteBanner(row.bannerId)

      ElMessage.success('删除成功')
      loadBannerList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交轮播图
const handleSubmitBanner = async () => {
  const valid = await bannerFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // TODO: 调用后端接口
    // if (isEdit.value) {
    //   await adminApi.updateBanner(bannerForm.bannerId, bannerForm)
    // } else {
    //   await adminApi.addBanner(bannerForm)
    // }

    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    bannerDialogVisible.value = false
    loadBannerList()
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// 重置轮播图表单
const resetBannerForm = () => {
  Object.assign(bannerForm, {
    bannerId: null,
    title: '',
    imageUrl: '',
    link: '',
    sort: 0,
    status: 1
  })
  fileList.value = []
  bannerFormRef.value?.resetFields()
}

onMounted(() => {
  loadBannerList()
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
