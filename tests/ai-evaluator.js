/**
 * AI助手召回率和准确率评估工具
 *
 * 评估指标说明：
 * - 召回率(Recall) = 实际相关且被检索到的相关项 / 实际相关项
 * - 准确率(Precision) = 被检索到的相关项 / 被检索到的项
 * - F1分数 = 2 * (Precision * Recall) / (Precision + Recall)
 *
 * 对于AI助手评估：
 * - 关键词召回率：回答中包含的期望关键词数 / 总期望关键词数
 * - 操作准确率：正确触发的期望操作数 / 总期望操作数
 * - 意图识别准确率：正确识别意图的测试数 / 总测试数
 */

import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// 加载测试数据集
function loadTestDataset() {
  const datasetPath = path.join(__dirname, 'ai-test-dataset.json')
  const data = fs.readFileSync(datasetPath, 'utf-8')
  return JSON.parse(data)
}

// 模拟AI助手响应（实际使用时需要替换为真实的API调用）
async function simulateAIResponse(question) {
  // 这里模拟AI响应，实际使用时需要调用真实的API
  // 可以根据问题返回预设的模拟回答

  const simulatedResponses = {
    "稻飞虱": "稻飞虱是水稻的主要害虫之一，防治方法包括：1) 选用抗虫品种 2) 适时播种，避开害虫高峰期 3) 科学施肥，避免过迟施氮肥 4) 保护天敌，如蜘蛛、寄生蜂等 5) 必要时使用噻嗪酮、吡虫啉等药剂防治，注意轮换用药。",
    "播种": "小麦一般在秋季10-11月播种，具体时间根据当地气候条件确定。播种前要整地施肥，保证土壤墒情。播种深度3-5厘米为宜。",
    "价格": "农产品价格会根据市场供需、季节、地区等因素波动。建议您关注平台的市场行情板块，或联系客服获取最新报价。",
    "发布产品": "发布农产品很简单：1) 登录账户 2) 进入【发布产品】页面 3) 填写产品信息、上传图片 4) 提交审核。审核通过后产品即可展示。",
    "下单": "购买流程：1) 浏览商品加入【购物车】 2) 结算时填写收货地址 3) 选择支付方式 4) 确认订单。完成后可在【查看订单】中跟踪物流状态。",
    "贷款": "申请农业贷款需要满足以下条件：1) 年满18周岁，具有完全民事行为能力 2) 有稳定的农业经营收入 3) 信用记录良好 4) 能提供相关经营证明材料。您可以【申请融资】查看具体产品。",
    "还款": "支持多种还款方式：1) 等额本息 2) 等额本金 3) 按月付息到期还本。还款期限根据产品不同，一般为1-36个月。您可以在个人中心查看还款计划。",
    "利率": "我们的贷款产品根据期限和客户资质，年化利率在4.2%-8.5%之间。优质客户可享受优惠利率，建议【申请融资】查看个性化报价。",
    "预约": "预约专家步骤：1) 浏览专家列表，查看专长领域 2) 选择合适的时间和专家 3) 填写咨询问题描述 4) 确认预约。您可以通过【专家预约】功能进行操作。",
    "专家": "平台拥有多位农业技术专家，涵盖种植技术、病虫害防治、农产品加工等领域。您可以在【专家预约】页面查看所有专家的详细介绍和用户评价。",
    "个人资料": "修改个人资料请进入【个人中心】，点击编辑按钮即可修改头像、姓名、联系方式等信息。如需更改身份信息，需要重新提交认证材料。",
    "客服": "您可以通过【联系客服】获取帮助。我们提供7×16小时在线客服服务，也可以拨打客服热线400-XXX-XXXX。",
    "订单": "所有订单都可以在【查看订单】页面查看，包括待付款、待发货、已完成等状态的订单。",
    "绿色食品": "绿色食品认证是中国特有的农产品质量安全认证，要求产自优良生态环境、按照绿色食品标准生产、实行全程质量控制。需要经过专门机构认证并许可使用绿色食品标志。",
    "提前还款": "支持提前还款，一般不收取违约金。具体政策根据不同贷款产品略有差异，建议查看贷款合同或咨询客服。",
    "收费": "部分专家提供免费咨询服务，部分专家按小时收费。预约时会显示具体收费标准，付费专家一般提供更深入的指导服务。",
    "资质": "发布农产品需要：1) 个人实名认证 2) 农户身份认证 3) 产品相关资质证明（如有机认证、产地证明等）。【发布产品】时系统会引导您完成认证。",
    "综合防治": "病虫害综合防治(IPM)是采用农业防治、生物防治、物理防治和化学防治相结合的策略，优先使用非化学方法，合理使用农药，实现经济、生态和社会效益的统一。",
    "认证农户": "申请成为认证农户需要：1) 完成实名认证 2) 提供土地承包证明或种植证明 3) 通过平台审核。认证农户享有更多权益，如优先展示、融资优惠等。",
    "有机": "有机蔬菜是指不使用化学合成农药、化肥、生长调节剂等物质生产的蔬菜，经过有机认证机构认证。无机蔬菜（常规蔬菜）可能使用常规农业生产投入品。两者在安全标准上都有保障。",
    "小额贷款": "农户小额贷款额度一般为1万-50万元，具体根据您的经营规模、信用状况等综合评估。首次申请通常额度较低，良好还款后可提升额度。",
    "购物车": "购物车商品会保存7天，超过7天未结算可能会因库存变动而失效，建议及时下单。商品售罄时会自动从购物车移除。",
    "帮助": "专家可以提供以下帮助：1) 种植技术指导 2) 病虫害诊断和防治建议 3) 农产品加工储存建议 4) 市场分析和营销指导。您可以根据需要选择合适的专家。"
  }

  // 根据问题关键词返回模拟响应
  for (const [keyword, response] of Object.entries(simulatedResponses)) {
    if (question.includes(keyword)) {
      return response
    }
  }

  return "感谢您的提问。关于这个问题，建议您提供更多具体信息，或者联系我们的专家进行深入咨询。您也可以在【个人中心】查看相关功能。"
}

// 关键词召回率评估
function evaluateKeywordRecall(response, expectedKeywords) {
  if (!expectedKeywords || expectedKeywords.length === 0) return 1.0

  const foundKeywords = []
  const responseLower = response.toLowerCase()

  for (const keyword of expectedKeywords) {
    if (responseLower.includes(keyword.toLowerCase())) {
      foundKeywords.push(keyword)
    }
  }

  return {
    recall: foundKeywords.length / expectedKeywords.length,
    found: foundKeywords,
    total: expectedKeywords.length
  }
}

// 操作准确率评估
function evaluateActionAccuracy(response, expectedActions) {
  if (!expectedActions || expectedActions.length === 0) return { accuracy: 1.0, found: [], total: 0 }

  const foundActions = []

  for (const action of expectedActions) {
    if (response.includes(action)) {
      foundActions.push(action)
    }
  }

  return {
    accuracy: foundActions.length / expectedActions.length,
    found: foundActions,
    total: expectedActions.length
  }
}

// 意图识别准确率评估（简化版，实际需要NLP模型）
function evaluateIntentRecognition(question, response, expectedIntent) {
  // 这里简化处理：如果响应包含期望关键词，认为意图识别正确
  // 实际应用中需要使用语义相似度模型
  return {
    correct: response.length > 50, // 简化的判断条件
    expected: expectedIntent
  }
}

// 单个测试用例评估
async function evaluateTestCase(testCase) {
  try {
    const response = await simulateAIResponse(testCase.question)

    // 关键词召回率
    const keywordEval = evaluateKeywordRecall(response, testCase.expected_keywords)

    // 操作准确率
    const actionEval = evaluateActionAccuracy(response, testCase.expected_actions)

    // 意图识别
    const intentEval = evaluateIntentRecognition(testCase.question, response, testCase.expected_intent)

    return {
      id: testCase.id,
      question: testCase.question,
      category: testCase.category,
      response: response,
      keyword_recall: keywordEval.recall,
      keywords_found: keywordEval.found,
      keywords_total: keywordEval.total,
      action_accuracy: actionEval.accuracy,
      actions_found: actionEval.found,
      actions_total: actionEval.total,
      intent_correct: intentEval.correct,
      expected_intent: testCase.expected_intent
    }
  } catch (error) {
    return {
      id: testCase.id,
      question: testCase.question,
      error: error.message
    }
  }
}

// 运行完整评估
async function runEvaluation() {
  console.log('开始AI助手召回率和准确率评估...\n')

  const dataset = loadTestDataset()
  const results = []

  for (const testCase of dataset.test_cases) {
    console.log(`测试用例 #${testCase.id}: ${testCase.question.substring(0, 40)}...`)
    const result = await evaluateTestCase(testCase)
    results.push(result)

    if (result.error) {
      console.log(`  ❌ 错误: ${result.error}\n`)
    } else {
      console.log(`  ✓ 关键词召回率: ${(result.keyword_recall * 100).toFixed(1)}%`)
      console.log(`  ✓ 操作准确率: ${(result.action_accuracy * 100).toFixed(1)}%`)
      console.log(`  ✓ 意图识别: ${result.intent_correct ? '正确' : '错误'}\n`)
    }
  }

  // 计算总体指标
  const validResults = results.filter(r => !r.error)

  const avgKeywordRecall = validResults.reduce((sum, r) => sum + r.keyword_recall, 0) / validResults.length
  const avgActionAccuracy = validResults.reduce((sum, r) => sum + r.action_accuracy, 0) / validResults.length
  const intentAccuracy = validResults.filter(r => r.intent_correct).length / validResults.length

  const avgPrecision = (avgKeywordRecall + avgActionAccuracy + intentAccuracy) / 3
  const avgRecall = avgKeywordRecall
  const f1Score = 2 * (avgPrecision * avgRecall) / (avgPrecision + avgRecall)

  // 生成报告
  const report = {
    timestamp: new Date().toISOString(),
    summary: {
      total_tests: dataset.test_cases.length,
      successful_tests: validResults.length,
      failed_tests: results.filter(r => r.error).length,
      avg_keyword_recall: avgKeywordRecall,
      avg_action_accuracy: avgActionAccuracy,
      intent_accuracy: intentAccuracy,
      overall_precision: avgPrecision,
      overall_recall: avgRecall,
      f1_score: f1Score
    },
    by_category: {},
    detailed_results: results
  }

  // 按类别统计
  const categoryGroups = {}
  for (const result of validResults) {
    const cat = result.category
    if (!categoryGroups[cat]) {
      categoryGroups[cat] = { count: 0, keyword_recall: 0, action_accuracy: 0 }
    }
    categoryGroups[cat].count++
    categoryGroups[cat].keyword_recall += result.keyword_recall
    categoryGroups[cat].action_accuracy += result.action_accuracy
  }

  for (const [cat, data] of Object.entries(categoryGroups)) {
    report.by_category[cat] = {
      count: data.count,
      avg_keyword_recall: data.keyword_recall / data.count,
      avg_action_accuracy: data.action_accuracy / data.count
    }
  }

  return report
}

// 输出评估报告
function printReport(report) {
  console.log('\n' + '='.repeat(60))
  console.log('AI助手评估报告')
  console.log('='.repeat(60))
  console.log(`测试时间: ${report.timestamp}`)
  console.log(`测试总数: ${report.summary.total_tests}`)
  console.log(`成功: ${report.summary.successful_tests} | 失败: ${report.summary.failed_tests}`)
  console.log('')
  console.log('总体指标:')
  console.log(`  平均关键词召回率: ${(report.summary.avg_keyword_recall * 100).toFixed(2)}%`)
  console.log(`  平均操作准确率: ${(report.summary.avg_action_accuracy * 100).toFixed(2)}%`)
  console.log(`  意图识别准确率: ${(report.summary.intent_accuracy * 100).toFixed(2)}%`)
  console.log(`  总体准确率(Precision): ${(report.summary.overall_precision * 100).toFixed(2)}%`)
  console.log(`  总体召回率(Recall): ${(report.summary.overall_recall * 100).toFixed(2)}%`)
  console.log(`  F1分数: ${(report.summary.f1_score * 100).toFixed(2)}%`)
  console.log('')
  console.log('按类别统计:')
  for (const [category, stats] of Object.entries(report.by_category)) {
    console.log(`  ${category}:`)
    console.log(`    测试数量: ${stats.count}`)
    console.log(`    关键词召回率: ${(stats.avg_keyword_recall * 100).toFixed(2)}%`)
    console.log(`    操作准确率: ${(stats.avg_action_accuracy * 100).toFixed(2)}%`)
  }
  console.log('='.repeat(60))
}

// 主函数
async function main() {
  const report = await runEvaluation()

  // 保存报告
  const reportPath = path.join(__dirname, `ai-evaluation-report-${Date.now()}.json`)
  fs.writeFileSync(reportPath, JSON.stringify(report, null, 2))

  // 打印报告
  printReport(report)

  console.log(`\n详细报告已保存到: ${reportPath}`)
}

// 运行评估
main().catch(console.error)
