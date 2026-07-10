<template>
  <div class="profile-edit-container">
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h1>编辑个人资料</h1>
    </div>

    <el-card class="form-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="info">
          <el-form
            :model="userForm"
            :rules="userRules"
            ref="userFormRef"
            label-width="100px"
            class="edit-form"
          >
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar :size="100" :src="userForm.avatar || '/default-avatar.png'" />
                <el-button type="primary" link @click="changeAvatar">更换头像</el-button>
              </div>
            </el-form-item>

            <el-form-item label="用户名" prop="userName">
              <el-input v-model="userForm.userName" disabled />
            </el-form-item>

            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>

            <el-form-item label="电话" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入电话号码" />
            </el-form-item>

            <el-form-item label="地址">
              <el-input
                v-model="userForm.address"
                type="textarea"
                :rows="3"
                placeholder="请输入地址"
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="saveUserInfo" :loading="saving">
                保存更改
              </el-button>
              <el-button @click="resetUserForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="修改密码" name="password">
          <el-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            label-width="100px"
            class="edit-form"
          >
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="changePassword" :loading="changingPassword">
                修改密码
              </el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('info')
const saving = ref(false)
const changingPassword = ref(false)
const userFormRef = ref()
const passwordFormRef = ref()

const userForm = reactive({
  userName: '',
  realName: '',
  email: '',
  phone: '',
  address: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的电话号码'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback()
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
    callback(new Error('请输入正确的邮箱地址'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const userRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  email: [{ validator: validateEmail, trigger: 'blur' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.data) {
      Object.assign(userForm, res.data)
    }
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

const saveUserInfo = async () => {
  try {
    await userFormRef.value.validate()
    saving.value = true
    await updateUserInfo(userForm)
    ElMessage.success('保存成功')
    // 更新store中的用户信息
    userStore.setUserInfo({ ...userStore.userInfo, ...userForm })
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '保存失败')
    }
  } finally {
    saving.value = false
  }
}

const resetUserForm = () => {
  loadUserInfo()
}

const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    changingPassword.value = true
    await updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    resetPasswordForm()
    // 跳转到登录页
    setTimeout(() => {
      userStore.logout()
      router.push('/login')
    }, 1500)
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '修改密码失败')
    }
  } finally {
    changingPassword.value = false
  }
}

const resetPasswordForm = () => {
  passwordFormRef.value?.resetFields()
}

const changeAvatar = () => {
  ElMessage.info('头像上传功能待实现')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.profile-edit-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 20px;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  color: #303133;
}

.form-card {
  min-height: 400px;
}

.edit-form {
  max-width: 500px;
  margin-top: 20px;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
</style>
