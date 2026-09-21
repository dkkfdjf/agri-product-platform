package com.agri.platform.service;

import com.agri.platform.config.TencentMaaProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiService {

    private final TencentMaaProperties tencentMaaProperties;
    /** 读取超时 tencent.maas.timeout（默认30s），由 WebConfig#aiRestTemplate 提供 */
    private final RestTemplate aiRestTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(TencentMaaProperties tencentMaaProperties,
                     @Qualifier("aiRestTemplate") RestTemplate aiRestTemplate) {
        this.tencentMaaProperties = tencentMaaProperties;
        this.aiRestTemplate = aiRestTemplate;
    }

    private static final String SYSTEM_PROMPT = "你是农产品融销一体平台的智能助手，可以帮助用户解答以下问题：\n\n" +
            "1. 农业业务咨询：种植技术、病虫害防治、农产品销售、市场价格等\n" +
            "2. 平台操作指导：如何发布产品、如何下单、如何申请融资等\n" +
            "3. 融资服务：贷款产品、申请流程、还款方式等\n" +
            "4. 专家服务：如何预约专家、查看专家专长等\n" +
            "5. 其他相关问题\n\n" +
            "请用简洁、友好的语气回答，必要时引导用户使用平台相关功能。\n\n" +
            "当提到以下快捷操作时，请用【操作名称】的格式标注：\n" +
            "- 【申请融资】 - 跳转到融资申请页面\n" +
            "- 【发布产品】 - 跳转到产品发布页面\n" +
            "- 【联系客服】 - 显示客服联系方式\n" +
            "- 【查看订单】 - 跳转到订单列表\n" +
            "- 【购物车】 - 跳转到购物车页面\n" +
            "- 【专家预约】 - 跳转到专家预约页面\n" +
            "- 【个人中心】 - 跳转到个人中心\n" +
            "- 【查看知识】 - 跳转到知识库页面";

    public String chat(String message, List<ChatMessage> history) {
        try {
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();

            // 添加系统提示词
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            messages.add(systemMessage);

            // 添加历史消息（最近10条）
            if (history != null && !history.isEmpty()) {
                int startIndex = Math.max(0, history.size() - 10);
                for (int i = startIndex; i < history.size(); i++) {
                    ChatMessage msg = history.get(i);
                    Map<String, String> messageMap = new HashMap<>();
                    messageMap.put("role", msg.getRole());
                    messageMap.put("content", msg.getContent());
                    messages.add(messageMap);
                }
            }

            // 添加当前消息
            Map<String, String> currentMessage = new HashMap<>();
            currentMessage.put("role", "user");
            currentMessage.put("content", message);
            messages.add(currentMessage);

            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", tencentMaaProperties.getModel());
            requestBody.put("messages", messages);
            requestBody.put("stream", false);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(tencentMaaProperties.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("调用腾讯云 MaaS API，模型：{}", tencentMaaProperties.getModel());

            // 发送请求
            ResponseEntity<String> response = aiRestTemplate.exchange(
                tencentMaaProperties.getApiUrl(),
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    String content = choices.get(0).path("message").path("content").asText();
                    log.info("腾讯云 API 调用成功");
                    return content;
                }
            }

            log.error("腾讯云 API 响应格式错误");
            return "抱歉，AI 服务暂时不可用，请稍后重试。";

        } catch (Exception e) {
            log.error("调用腾讯云 MaaS API 失败", e);
            return "抱歉，AI 服务调用失败：" + e.getMessage();
        }
    }

    public String chatWithContext(String message, List<ChatMessage> history, String context) {
        String enhancedPrompt = buildRagPrompt(message, context);
        return chat(enhancedPrompt, history);
    }

    private String buildRagPrompt(String message, String context) {
        return String.format(
            "你是农产品融销一体平台的智能助手。请根据以下参考信息回答用户问题：\n\n" +
            "【参考信息】\n%s\n\n" +
            "【注意事项】\n" +
            "1. 优先使用参考信息中的内容回答\n" +
            "2. 如果参考信息不足，可以基于农业知识补充\n" +
            "3. 保持简洁、友好的语气\n" +
            "4. 回答时提及\"根据平台知识库...\"增强可信度\n\n" +
            "用户问题：%s",
            context, message
        );
    }

    public static class ChatMessage {
        private String role;
        private String content;

        public ChatMessage() {}

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
