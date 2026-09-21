# Task 13 Brief: 在 MainLayout 中集成 AI 助手

## 任务目标
修改 src/layouts/MainLayout.vue 文件，添加 AIAssistant 组件。

## 文件位置
- 工作目录: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend`
- 修改文件: `src/layouts/MainLayout.vue`

## 修改内容

### Template 部分
在 `</div>` (最外层 main-layout div) 之前添加:
```vue
<AIAssistant v-if="userStore.isLoggedIn()" />
```

### Script 部分
- 导入 useUserStore
- 导入 AIAssistant 组件

## 执行步骤

### Step 1: 修改 MainLayout.vue
（完整代码见计划文件）

### Step 2: 提交
```bash
git add src/layouts/MainLayout.vue
git commit -m "feat: integrate AI assistant into MainLayout"
```

## 全局约束
- 仅对已登录用户显示: `v-if="userStore.isLoggedIn()"`

## 报告文件
将执行结果写入: `C:\Users\34392\Desktop\agri-product-platform\frontend\agri-product-platform-frontend\.superpowers\sdd\task-13-report.md`
