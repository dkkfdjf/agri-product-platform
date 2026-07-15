<template>
  <div class="admin-banner">
    <div class="header">
      <h2>轮播图管理</h2>
      <el-button type="primary" @click="showAddDialog">添加轮播图</el-button>
    </div>

    <el-table :data="banners" style="width: 100%" v-loading="loading">
      <el-table-column label="图片" width="200">
        <template #default="{ row }">
          <img :src="row.picPath" class="banner-preview" @error="handleImageError" />
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link type="primary" @click="editBanner(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteBanner(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="editingBanner ? '编辑轮播图' : '添加轮播图'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="图片URL">
          <el-input v-model="form.picPath" placeholder="输入图片URL" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBanners, addBanner, updateBanner, deleteBanner as deleteBan } from '@/api/admin'

const loading = ref(false)
const saving = ref(false)
const banners = ref([])
const dialogVisible = ref(false)
const editingBanner = ref(null)

const form = reactive({
  picPath: '',
  sortOrder: 0
})

onMounted(() => {
  loadBanners()
})

const loadBanners = async () => {
  loading.value = true
  try {
    const res = await getBanners()
    banners.value = res.data || []
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  editingBanner.value = null
  form.picPath = ''
  form.sortOrder = 0
  dialogVisible.value = true
}

const editBanner = (banner) => {
  editingBanner.value = banner
  form.picPath = banner.picPath
  form.sortOrder = banner.sortOrder || 0
  dialogVisible.value = true
}

const save = async () => {
  if (!form.picPath) {
    ElMessage.warning('请输入图片URL')
    return
  }

  saving.value = true
  try {
    if (editingBanner.value) {
      await updateBanner(editingBanner.value.bannerId, form)
      ElMessage.success('更新成功')
    } else {
      await addBanner(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    await loadBanners()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const deleteBanner = async (banner) => {
  try {
    await ElMessageBox.confirm('确定删除此轮播图吗？', '提示', { type: 'warning' })
    await deleteBan(banner.bannerId)
    ElMessage.success('删除成功')
    await loadBanners()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleImageError = (e) => {
  e.target.style.display = 'none'
}
</script>

<style scoped>
.admin-banner {
  max-width: 1000px;
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

.banner-preview {
  width: 150px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
</style>
