/**
 * AI API 模块 - 腾讯云 MaaS 集成
 */
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

/**
 * 获取最近的消息历史
 * @param {Array} allMessages - 完整的消息历史
 * @param {Number} maxCount - 最多保留的消息数量
 * @returns {Array} 最近的消息列表
 */
export function getRecentMessages(allMessages, maxCount = 10) {
  if (!Array.isArray(allMessages)) {
    return []
  }

  // 返回最近的 maxCount 条消息，从数组末尾开始截取
  return allMessages.slice(-maxCount)
}

/**
 * 发送消息到腾讯云 MaaS API
 * @param {Array} messages - 消息历史数组，格式: [{ role: 'user'|'assistant', content: string }]
 * @returns {Promise<string>} AI 的回复内容
 */
export async function sendMessage(messages) {
  // 验证输入
  if (!Array.isArray(messages)) {
    throw new Error('消息格式错误：必须是数组')
  }

  // 获取 API 配置
  const apiUrl = import.meta.env.VITE_TENCENT_API_URL
  const apiKey = import.meta.env.VITE_TENCENT_API_KEY

  if (!apiUrl || !apiKey) {
    throw new Error('腾讯云 API 配置缺失，请检查环境变量')
  }

  // 构建完整的消息列表（包含系统提示词）
  const fullMessages = [
    { role: 'system', content: SYSTEM_PROMPT },
    ...messages
  ]

  try {
    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey}`
      },
      body: JSON.stringify({
        model: import.meta.env.VITE_TENCENT_MODEL || 'hy3-preview',
        messages: fullMessages,
        stream: false
      })
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`API 请求失败: ${response.status} ${errorText}`)
    }

    const data = await response.json()

    // 验证响应格式 - 腾讯云API格式
    if (!data.choices || !data.choices[0] || !data.choices[0].message) {
      throw new Error('API 响应格式错误')
    }

    return data.choices[0].message.content
  } catch (error) {
    console.error('腾讯云 API 调用错误:', error)
    throw error
  }
}
