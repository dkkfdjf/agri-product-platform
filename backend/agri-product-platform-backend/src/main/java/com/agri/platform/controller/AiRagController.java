package com.agri.platform.controller;

import com.agri.platform.common.Result;
import com.agri.platform.dto.AiRagRequest;
import com.agri.platform.dto.AiRagResponse;
import com.agri.platform.service.AiRagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiRagController {

    private final AiRagService aiRagService;

    @PostMapping("/rag")
    public Result<AiRagResponse> chatWithRag(@RequestBody AiRagRequest request) {
        try {
            log.info("收到 RAG 聊天请求，消息：{}，使用 RAG：{}", request.getMessage(), request.getUseRag());

            // TODO: Get user role from JWT context
            String userRole = "farmer"; // Default for now

            AiRagResponse response = aiRagService.chatWithRag(request, userRole);
            return Result.success(response);
        } catch (Exception e) {
            log.error("RAG 聊天失败", e);
            return Result.error("AI 服务暂时不可用：" + e.getMessage());
        }
    }

    @GetMapping("/vector/status")
    public Result<String> getVectorStatus() {
        return Result.success("RAG system operational");
    }

    @PostMapping("/vector/rebuild")
    public Result<String> rebuildVectors() {
        // TODO: Implement vector rebuild logic
        return Result.success("Vector rebuild initiated");
    }
}
