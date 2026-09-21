# AI助手召回率和准确率测试

本测试框架用于评估农产品融销一体平台AI助手的召回率和准确率。

## 测试指标说明

### 召回率 (Recall)
召回率衡量AI助手能够检索到相关信息的能力。

**公式**: `召回率 = 实际相关且被检索到的相关项 / 实际相关项`

**在本测试中**: 关键词召回率 = 回答中包含的期望关键词数 / 总期望关键词数

### 准确率 (Precision)
准确率衡量AI助手检索结果的准确性。

**公式**: `准确率 = 被检索到的相关项 / 被检索到的项`

**在本测试中**: 
- 关键词准确率：类似召回率，因为我们将包含关键词作为相关
- 操作准确率：正确触发的期望操作数 / 总期望操作数
- 意图识别准确率：正确识别意图的测试数 / 总测试数

### F1分数
F1分数是准确率和召回率的调和平均值，综合评估模型性能。

**公式**: `F1 = 2 × (Precision × Recall) / (Precision + Recall)`

## 测试数据集

测试数据集包含25个测试用例，分为以下类别：

| 类别 | 测试用例数 | 覆盖内容 |
|------|-----------|----------|
| 农业业务咨询 | 9 | 种植技术、病虫害防治、市场价格、认证知识 |
| 平台操作指导 | 9 | 发布产品、下单、个人设置、客服联系 |
| 融资服务 | 5 | 贷款条件、还款方式、利率、提前还款 |
| 专家服务 | 3 | 预约流程、专家查询、费用咨询 |

## 使用方法

### 1. 安装依赖
```bash
cd tests
npm init -y
```

### 2. 配置环境变量
创建 `.env` 文件：
```env
VITE_TENCENT_API_URL=你的API地址
VITE_TENCENT_API_KEY=你的API密钥
VITE_TENCENT_MODEL=hy3-preview
```

### 3. 运行测试
```bash
node ai-evaluator.js
```

### 4. 查看报告
测试完成后会生成两个文件：
- `ai-evaluation-report-{timestamp}.json` - 详细JSON报告
- 控制台输出 - 简要报告

## 测试结果解读

### 优秀标准
- 关键词召回率 ≥ 80%
- 操作准确率 ≥ 90%
- 意图识别准确率 ≥ 85%
- F1分数 ≥ 80%

### 良好标准
- 关键词召回率 ≥ 60%
- 操作准确率 ≥ 75%
- 意图识别准确率 ≥ 70%
- F1分数 ≥ 65%

### 需要改进
- 任一指标低于良好标准

## 扩展测试

### 添加新测试用例
在 `ai-test-dataset.json` 中添加新测试用例：

```json
{
  "id": 26,
  "category": "农业业务咨询",
  "question": "你的问题",
  "expected_keywords": ["关键词1", "关键词2"],
  "expected_actions": ["【操作名称】"],
  "expected_intent": "意图描述"
}
```

### 自定义AI响应
修改 `ai-evaluator.js` 中的 `simulateAIResponse` 函数，替换为真实的API调用：

```javascript
async function simulateAIResponse(question) {
  const response = await fetch('YOUR_API_ENDPOINT', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ question })
  })
  return response.text()
}
```

## 持续改进建议

1. **定期运行测试** - 每次AI模型更新后运行测试
2. **扩充测试集** - 根据用户反馈添加新的测试用例
3. **A/B测试** - 对比不同提示词或模型的效果
4. **真实用户反馈** - 结合用户满意度评分进行综合评估
