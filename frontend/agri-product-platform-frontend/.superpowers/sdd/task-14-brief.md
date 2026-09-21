# Task 14 Brief: 在独立布局中集成 AI 助手

## 任务目标
修改 src/layouts/ExpertLayout.vue、BankLayout.vue、AdminLayout.vue 文件，添加 AIAssistant 组件。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 修改文件:
  - `src/layouts/ExpertLayout.vue`
  - `src/layouts/BankLayout.vue`
  - `src/layouts/AdminLayout.vue`

## 修改内容

每个布局文件都需要:
1. 在 template 中添加: `<AIAssistant v-if="userStore.isLoggedIn()" />`
2. 在 script 中导入: `import { AIAssistant } from '@/components/AIAssistant'`
3. 在 script 中导入: `import { useUserStore } from '@/stores/user'`
4. 添加: `const userStore = useUserStore()`

## 执行步骤

### Step 1-3: 修改三个布局文件
（完整代码见计划文件）

### Step 4: 提交
```bash
git add src/layouts/ExpertLayout.vue src/layouts/BankLayout.vue src/layouts/AdminLayout.vue
git commit -m "feat: integrate AI assistant into independent layouts"
```

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-14-report.md`
