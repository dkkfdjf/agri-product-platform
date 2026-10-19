/**
 * AI助手真实API评估工具(调用腾讯云 MaaS 生产同款接口与系统提示词)
 *
 * 与 ai-evaluator.js 的区别:
 * - ai-evaluator.js 使用 simulateAIResponse() 的关键词匹配模拟应答,不调用真实模型;
 *   其产出的报告只能反映"数据集与模拟应答的匹配度",不能代表线上 AI 助手的真实水平。
 * - 本工具逐字复刻前端 src/api/ai.js 的调用:同一 SYSTEM_PROMPT、同一模型(hy3-preview)、
 *   同一 OpenAI 兼容请求体,逐条调用真实 API,并额外记录每次请求延迟。
 *
 * 用法: node tests/ai-evaluator-real.mjs
 * 配置: 从 frontend/agri-product-platform-frontend/.env 读取 VITE_TENCENT_* 三项
 */

import fs from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// ===== 读取前端同款配置 =====
function loadEnv() {
  const envPath = path.join(__dirname, '..', 'frontend', 'agri-product-platform-frontend', '.env')
  const txt = fs.readFileSync(envPath, 'utf8')
  const pick = (k) => (txt.match(new RegExp(`^${k}=(.*)$`, 'm')) || [])[1]?.trim()
  return { url: pick('VITE_TENCENT_API_URL'), key: pick('VITE_TENCENT_API_KEY'), model: pick('VITE_TENCENT_MODEL') }
}

// ===== 与前端 src/api/ai.js 完全一致的 SYSTEM_PROMPT =====
const SYSTEM_PROMPT = `你是农产品融销一体平台的智能助手，可以帮助用户解答以下问题：

1. 农业业务咨询：种植技术、病虫害防治、农产品销售、市场价格等
2. 平台操作指导：如何发布产品、如何下单、如何申请融资等
3. 融资服务：贷款产品、申请流程、还款方式等
4. 专家服务：如何预约专家、查看专家专长等
5. 其他相关问题

请用简洁、友好的语气回答，必要时引导用户使用平台相关功能。

当提到以下快捷操作时，请用【操作名称】的格式标注：
- 【申请融资】 - 跳转到融资申请页面
- 【发布产品】 - 跳转到产品发布页面
- 【联系客服】 - 显示客服联系方式
- 【查看订单】 - 跳转到订单列表
- 【购物车】 - 跳转到购物车页面
- 【专家预约】 - 跳转到专家预约页面
- 【个人中心】 - 跳转到个人中心
- 【查看知识】 - 跳转到知识库页面`

// ===== 真实 API 调用(复刻 sendMessage:system + 单条 user 消息,非流式) =====
async function realAIResponse(question, cfg) {
  const ctrl = new AbortController()
  const timer = setTimeout(() => ctrl.abort(), 60000)
  const t0 = Date.now()
  try {
    const resp = await fetch(cfg.url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${cfg.key}` },
      body: JSON.stringify({
        model: cfg.model || 'hy3-preview',
        messages: [{ role: 'system', content: SYSTEM_PROMPT }, { role: 'user', content: question }],
        stream: false
      }),
      signal: ctrl.signal
    })
    if (!resp.ok) throw new Error(`API ${resp.status}: ${(await resp.text()).slice(0, 200)}`)
    const data = await resp.json()
    if (!data.choices?.[0]?.message) throw new Error('响应格式错误')
    return { text: data.choices[0].message.content, latencyMs: Date.now() - t0 }
  } finally {
    clearTimeout(timer)
  }
}

// ===== 评估逻辑(与 ai-evaluator.js 保持一致,便于同口径对比) =====
function evaluateKeywordRecall(response, expectedKeywords) {
  if (!expectedKeywords || expectedKeywords.length === 0) return { recall: 1.0, found: [], total: 0 }
  const lower = response.toLowerCase()
  const found = expectedKeywords.filter((k) => lower.includes(k.toLowerCase()))
  return { recall: found.length / expectedKeywords.length, found, total: expectedKeywords.length }
}

function evaluateActionAccuracy(response, expectedActions) {
  if (!expectedActions || expectedActions.length === 0) return { accuracy: 1.0, found: [], total: 0 }
  const found = expectedActions.filter((a) => response.includes(a))
  return { accuracy: found.length / expectedActions.length, found, total: expectedActions.length }
}

// 意图判定:沿用原工具口径(有效回答视为意图命中),但要求命中至少一个期望关键词,
// 比原"回答>50字"的口径更严格,也更接近"意图识别"的真实含义
function evaluateIntent(question, response, expectedKeywords) {
  if (!response || response.length <= 50) return { correct: false }
  if (expectedKeywords && expectedKeywords.length > 0) {
    const lower = response.toLowerCase()
    return { correct: expectedKeywords.some((k) => lower.includes(k.toLowerCase())) }
  }
  return { correct: true }
}

async function main() {
  const cfg = loadEnv()
  if (!cfg.url || !cfg.key) throw new Error('未读取到 VITE_TENCENT_* 配置(frontend/.env)')
  // 支持命令行覆盖: node ai-evaluator-real.mjs <model> [apiUrl]
  // (原 .env 中 hy3-preview 已在腾讯侧下线,评估时需指定当前可用模型;
  //   .env 的 -intl 国际端点在境内网络下延迟极高,可改用国内端点)
  if (process.argv[2]) cfg.model = process.argv[2]
  if (process.argv[3]) cfg.url = process.argv[3]
  console.log(`目标: ${cfg.url}  模型: ${cfg.model}  数据集: ai-test-dataset.json\n`)

  const dataset = JSON.parse(fs.readFileSync(path.join(__dirname, 'ai-test-dataset.json'), 'utf8'))
  const results = []

  for (const tc of dataset.test_cases) {
    process.stdout.write(`#${tc.id} ${tc.question.slice(0, 30)}... `)
    try {
      const { text, latencyMs } = await realAIResponse(tc.question, cfg)
      const kw = evaluateKeywordRecall(text, tc.expected_keywords)
      const ac = evaluateActionAccuracy(text, tc.expected_actions)
      const it = evaluateIntent(tc.question, text, tc.expected_keywords)
      results.push({
        id: tc.id, question: tc.question, category: tc.category,
        response: text, latency_ms: latencyMs,
        keyword_recall: kw.recall, keywords_found: kw.found, keywords_total: kw.total,
        action_accuracy: ac.accuracy, actions_found: ac.found, actions_total: ac.total,
        intent_correct: it.correct
      })
      console.log(`${latencyMs}ms 召回${(kw.recall * 100).toFixed(0)}% 动作${(ac.accuracy * 100).toFixed(0)}% 意图${it.correct ? '✓' : '✗'}`)
    } catch (e) {
      results.push({ id: tc.id, question: tc.question, error: e.message })
      console.log(`错误: ${e.message.slice(0, 80)}`)
    }
    await new Promise((r) => setTimeout(r, 500))
  }

  const ok = results.filter((r) => !r.error)
  const lat = ok.map((r) => r.latency_ms).sort((a, b) => a - b)
  const avg = (xs) => xs.reduce((s, x) => s + x, 0) / xs.length
  const summary = {
    tester: 'ai-evaluator-real.mjs(真实腾讯云 MaaS 调用,生产同款 SYSTEM_PROMPT)',
    model: cfg.model,
    total_tests: dataset.test_cases.length,
    successful_tests: ok.length,
    failed_tests: results.filter((r) => r.error).length,
    avg_keyword_recall: avg(ok.map((r) => r.keyword_recall)),
    avg_action_accuracy: avg(ok.map((r) => r.action_accuracy)),
    intent_accuracy_strict: ok.filter((r) => r.intent_correct).length / ok.length,
    latency: {
      avg_ms: Math.round(avg(lat)),
      p50_ms: lat[Math.floor(lat.length * 0.5)],
      p95_ms: lat[Math.floor(lat.length * 0.95)],
      max_ms: lat[lat.length - 1]
    }
  }
  const report = { timestamp: new Date().toISOString(), summary, detailed_results: results }
  const out = path.join(__dirname, `ai-evaluation-report-real-${Date.now()}.json`)
  fs.writeFileSync(out, JSON.stringify(report, null, 2))

  console.log('\n========== 真实 API 评估报告 ==========')
  console.log(`成功 ${summary.successful_tests}/${summary.total_tests}`)
  console.log(`关键词召回率(均值): ${(summary.avg_keyword_recall * 100).toFixed(1)}%`)
  console.log(`动作准确率(均值):   ${(summary.avg_action_accuracy * 100).toFixed(1)}%`)
  console.log(`意图命中(严格口径): ${(summary.intent_accuracy_strict * 100).toFixed(1)}%`)
  console.log(`延迟: 平均 ${summary.latency.avg_ms}ms / P95 ${summary.latency.p95_ms}ms / 最大 ${summary.latency.max_ms}ms`)
  console.log(`报告: ${out}`)
}

main().catch((e) => { console.error(e); process.exit(1) })
