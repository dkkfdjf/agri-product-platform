package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<String> chat(@RequestBody ChatRequest request) {
        try {
            log.info("收到AI聊天请求，消息：{}", request.getMessage());

            String reply = aiService.chat(request.getMessage(), request.getHistory());
            return Result.success(reply);
        } catch (Exception e) {
            log.error("AI聊天失败", e);
            return Result.error("AI服务暂时不可用：" + e.getMessage());
        }
    }

    public static class ChatRequest {
        private String message;
        private List<AiService.ChatMessage> history;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<AiService.ChatMessage> getHistory() {
            return history;
        }

        public void setHistory(List<AiService.ChatMessage> history) {
            this.history = history;
        }
    }
}
