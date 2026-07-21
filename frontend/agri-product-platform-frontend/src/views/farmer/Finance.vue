<template>
  <div class="finance-page">
    <div class="page-header">
      <h1 class="page-title">融资申请</h1>
      <el-button type="primary" @click="showApplyDialog">
        <el-icon><Plus /></el-icon>
        申请融资
      </el-button>
    </div>

    <!-- 融资产品推荐 -->
    <el-card class="products-card" v-if="financeProducts.length > 0">
      <template #header>
        <span>推荐融资产品</span>
      </template>
      <div class="products-grid">
        <div v-for="product in financeProducts" :key="product.productId" class="product-item">
          <div class="product-name">{{ product.bankName }}</div>
          <div class="product-desc">{{ product.introduce }}</div>
          <div class="product-terms">
            <div class="term">
              <span class="label">年利率：</span>
              <span class="value">{{ product.rate }}%</span>
            </div>
            <div class="term">
              <span class="label">期限：</span>
              <span class="value">{{ product.repayment }}个月</span>
            </div>
            <div class="term">
              <span class="label">额度：</span>
              <span class="value">¥{{ product.money }}</span>
            </div>
          </div>
          <el-button type="primary" size="small" @click="applyForProduct(product)">
            立即申请
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 我的融资申请 -->
    <el-card class="applications-card" v-loading="loading">
      <template #header>
        <span>我的融资申请</span>
      </template>
      <el-table :data="applications" style="width: 100%">
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="money" label="申请金额(元)" width="140">
          <template #default="{ row }">
            ¥{{ row.money }}
          </template>
        </el-table-column>
        <el-table-column prop="repayment" label="期限(月)" width="100" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="purpose" label="申请原因" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="150">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              link
              type="primary"
              @click="editApplication(row)"
            >
              修改
            </el-button>
            <el-button
              v-if="row.status === 0"
              link
              type="danger"
              @click="cancelApplication(row)"
            >
              撤销
            </el-button>
            <el-button v-else link type="info" @click="viewDetails(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadApplications"
          @current-change="loadApplications"
        />
      </div>

      <el-empty v-if="!loading && applications.length === 0" description="暂无融资申请" />
    </el-card>

    <!-- 申请融资对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="申请融资"
      width="600px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="融资产品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择融资产品" style="width: 100%">
            <el-option
              v-for="product in financeProducts"
              :key="product.productId"
              :label="product.bankName"
              :value="product.productId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idNum">
          <el-input v-model="form.idNum" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="申请金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :min="1"
            :max="500"
            :step="1"
            :precision="2"
            style="width: 100%"
          />
          <span class="unit-text">万元</span>
        </el-form-item>
        <el-form-item label="申请原因" prop="purpose">
          <el-input
            v-model="form.purpose"
            type="textarea"
            :rows="3"
            placeholder="请说明融资用途，如：购买农机具、建设大棚、采购农资等"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="还款来源" prop="repaymentSource">
          <el-input
            v-model="form.repaymentSource"
            type="textarea"
            :rows="3"
            placeholder="请说明还款来源，如：农产品销售收入、经营收入等"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-divider content-position="left">联合贷款人（选填）</el-divider>
        <div class="combination-tip">
          选择<b>农户</b>联系人作为联合贷款人，提交后对方会收到邀请通知，<b>对方同意后</b>其姓名/电话/身份证号将自动绑定到本申请。最多 2 人，有助于提升审批通过率。
        </div>
        <el-form-item label="联合人1">
          <el-select
            v-model="form.jointUserName1"
            filterable
            clearable
            placeholder="选择联系人（姓名 + 电话）"
            style="width: 100%"
          >
            <el-option
              v-for="c in contactOptions"
              :key="c.userName"
              :label="`${c.realName || c.userName}（${c.phone || '无电话'}）`"
              :value="c.userName"
              :disabled="c.userName === form.jointUserName2"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="联合人2">
          <el-select
            v-model="form.jointUserName2"
            filterable
            clearable
            placeholder="选择联系人（姓名 + 电话）"
            style="width: 100%"
          >
            <el-option
              v-for="c in contactOptions"
              :key="c.userName"
              :label="`${c.realName || c.userName}（${c.phone || '无电话'}）`"
              :value="c.userName"
              :disabled="c.userName === form.jointUserName1"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApplication" :loading="submitting">
          提交申请
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getFinanceProducts, applyFinance, cancelFinance, getMyFinanceApplications } from '@/api/finance'
import { getContacts } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const financeProducts = ref([])
const applications = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const formRef = ref(null)
const editingApplicationId = ref(null)

// 联系人列表（农户/买家/专家，排除自己），用于选择联合贷款人
const contactOptions = ref([])

// 申请表单：字段与后端 FinanceRequest / tb_finance 对齐
// amount 为页面输入(万元)，提交时换算为 money(元)
const form = reactive({
  productId: null,
  realName: '',
  phone: '',
  idNum: '',
  amount: 10,
  purpose: '',
  repaymentSource: '',
  // 联合贷款人（选填，最多 2 人）—— 选择联系人(userName)，提交后由后端创建邀请，对方同意后回填资料
  jointUserName1: '',
  jointUserName2: ''
})

const rules = {
  productId: [{ required: true, message: '请选择融资产品', trigger: 'change' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idNum: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入申请金额', trigger: 'blur' }],
  purpose: [{ required: true, message: '请输入申请原因', trigger: 'blur' }],
  repaymentSource: [{ required: true, message: '请输入还款来源', trigger: 'blur' }]
}

// 加载融资产品
const loadFinanceProducts = async () => {
  try {
    const res = await getFinanceProducts({ page: 1, pageSize: 10 })
    if (res.code === 200) {
      financeProducts.value = res.data.records || []
    }
  } catch (error) {
    ElMessage.error('加载融资产品失败')
  }
}

// 加载我的融资申请
const loadApplications = async () => {
  loading.value = true
  try {
    const res = await getMyFinanceApplications({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      applications.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('加载融资申请失败')
  } finally {
    loading.value = false
  }
}

// 加载联系人列表（供联合贷款人选择；联合贷款人只能选农户）
const loadContacts = async () => {
  try {
    const res = await getContacts({ role: 'farmer' })
    contactOptions.value = res.data || []
  } catch (e) {
    // 忽略：加载失败只是没有联系人可选
  }
}

// 打开表单时用个人资料预填 申请人 姓名/电话/身份证号（仅空值时填，不覆盖已输入）
const ensureApplicantDefaults = () => {
  const info = userStore.userInfo
  if (!info) return
  if (!form.realName && info.realName) form.realName = info.realName
  if (!form.phone && info.phone) form.phone = info.phone
  if (!form.idNum && info.identityNum) form.idNum = info.identityNum
}

// 显示申请对话框
const showApplyDialog = () => {
  ensureApplicantDefaults()
  dialogVisible.value = true
}

// 针对产品申请
const applyForProduct = (product) => {
  form.productId = product.productId
  ensureApplicantDefaults()
  dialogVisible.value = true
}

// 编辑申请
const editApplication = (application) => {
  editingApplicationId.value = application.financeId || application.id
  Object.assign(form, {
    productId: application.productId,
    realName: application.realName || '',
    phone: application.phone || '',
    idNum: application.idNum || '',
    amount: application.money ? application.money / 10000 : 10, // 元换算回万元
    purpose: application.purpose || '',
    repaymentSource: application.repaymentSource || '',
    // 编辑时不回填联合人（联合人由邀请流程管理，不在表单里直接改）
    jointUserName1: '',
    jointUserName2: ''
  })
  dialogVisible.value = true
}

// 提交申请
const submitApplication = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitting.value = true
    try {
      // 与后端 FinanceRequest 字段一致：money 为元(NOT NULL)
      const data = {
        productId: form.productId,
        realName: form.realName,
        phone: form.phone,
        idNum: form.idNum,
        money: form.amount * 10000, // 万元转换为元
        purpose: form.purpose,
        repaymentSource: form.repaymentSource,
        // 联合贷款人：传所选联系人 userName，后端创建邀请；combination 资料由对方同意后回填
        jointUserName1: form.jointUserName1 || null,
        jointUserName2: form.jointUserName2 || null
      }

      if (editingApplicationId.value) {
        // TODO: 更新申请API
        ElMessage.success('申请修改成功')
      } else {
        await applyFinance(data)
        ElMessage.success('融资申请已提交，请等待银行审核')
      }

      dialogVisible.value = false
      await loadApplications()
    } catch (error) {
      // 展示后端返回的真实失败原因，便于定位（业务错误/HTTP错误均覆盖）
      const msg = error?.response?.data?.message || error?.message || '提交申请失败'
      ElMessage.error(msg)
    } finally {
      submitting.value = false
    }
  })
}

// 撤销申请
const cancelApplication = async (application) => {
  try {
    await ElMessageBox.confirm('撤销后该申请将被永久删除，确定要撤销吗？', '撤销确认', {
      type: 'warning'
    })
    await cancelFinance(application.financeId)
    ElMessage.success('申请已撤销')
    await loadApplications()
  } catch (error) {
    if (error !== 'cancel') {
      const msg = error?.response?.data?.message || error?.message || '撤销申请失败'
      ElMessage.error(msg)
    }
  }
}

// 查看详情
const viewDetails = (application) => {
  ElMessage.info('查看详情功能待实现')
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}

// 获取状态类型（后端 status 为整数：0申请中 1已通过 2已驳回）
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    0: '待审核',
    1: '已批准',
    2: '已拒绝'
  }
  return textMap[status] || status
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(form, {
    productId: null,
    realName: '',
    phone: '',
    idNum: '',
    amount: 10,
    purpose: '',
    repaymentSource: '',
    jointUserName1: '',
    jointUserName2: ''
  })
  editingApplicationId.value = null
}

onMounted(() => {
  loadFinanceProducts()
  loadApplications()
  loadContacts()
})
</script>

<style scoped>
.finance-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.product-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-name {
  font-weight: 600;
  font-size: 16px;
}

.product-desc {
  font-size: 14px;
  color: #666;
  flex: 1;
}

.product-terms {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 14px;
}

.term .label {
  color: #666;
}

.term .value {
  font-weight: 500;
}

.unit-text {
  margin-left: 8px;
  color: #666;
  font-size: 14px;
}

.combination-tip {
  margin: 0 0 16px 120px;
  padding: 8px 12px;
  font-size: 13px;
  color: #8c6d2f;
  background: #fdf6e3;
  border-left: 3px solid #e6a23c;
  border-radius: 4px;
  line-height: 1.5;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
