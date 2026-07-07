<template>
  <div class="expert-profile">
    <h2>账户设置</h2>

    <el-tabs v-model="activeTab">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <el-card>
          <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="120px">
            <el-form-item label="账号">
              <el-input v-model="userForm.userName" disabled />
            </el-form-item>
            <el-form-item label="专家姓名" prop="expertName">
              <el-input v-model="userForm.expertName" placeholder="请输入专家姓名" />
            </el-form-item>
            <el-form-item label="专业领域" prop="specialty">
              <el-input v-model="userForm.specialty" placeholder="请输入专业领域" />
            </el-form-item>
            <el-form-item label="职称" prop="title">
              <el-select v-model="userForm.title" placeholder="请选择职称" style="width: 100%">
                <el-option label="初级农艺师" value="初级农艺师" />
                <el-option label="农艺师" value="农艺师" />
                <el-option label="高级农艺师" value="高级农艺师" />
                <el-option label="研究员" value="研究员" />
                <el-option label="教授" value="教授" />
              </el-select>
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input v-model="userForm.bio" type="textarea" :rows="4" placeholder="请输入个人简介" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateInfo" :loading="updating">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 修改密码 -->
      <el-tab-pane label="修改密码" name="password">
        <el-card>
          <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword" :loading="updating">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const activeTab = ref('basic')
const updating = ref(false)
const userFormRef = ref(null)
const passwordFormRef = ref(null)

// 用户信息表单
const userForm = reactive({
  userName: '',
  expertName: '',
  specialty: '',
  title: '',
  phone: '',
  bio: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const userRules = {
  expertName: [{ required: true, message: '请输入专家姓名', trigger: 'blur' }],
  specialty: [{ required: true, message: '请输入专业领域', trigger: 'blur' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  bio: [{ required: true, message: '请输入个人简介', trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 加载用户信息
const loadUserInfo = () => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    Object.assign(userForm, {
      userName: userInfo.userName || '',
      expertName: userInfo.expertName || '',
      specialty: userInfo.specialty || '',
      title: userInfo.title || '',
      phone: userInfo.phone || '',
      bio: userInfo.bio || ''
    })
  }
}

// 更新基本信息
const handleUpdateInfo = async () => {
  const valid = await userFormRef.value?.validate().catch(() => false)
  if (!valid) return

  updating.value = true
  try {
    // TODO: 调用后端接口
    // await userApi.updateInfo(userForm)

    // 更新本地store
    userStore.setUserInfo({ ...userStore.userInfo, ...userForm })

    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    updating.value = false
  }
}

// 修改密码
const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return

  updating.value = true
  try {
    // TODO: 调用后端接口
    // await userApi.updatePassword({
    //   oldPassword: passwordForm.oldPassword,
    //   newPassword: passwordForm.newPassword
    // })

    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    window.location.href = '/login'
  } catch (error) {
    ElMessage.error('密码修改失败')
  } finally {
    updating.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
</style>
