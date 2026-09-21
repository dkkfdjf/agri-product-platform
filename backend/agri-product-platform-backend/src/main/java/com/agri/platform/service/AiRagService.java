package com.agri.platform.service;

import com.agri.platform.dto.AiRagRequest;
import com.agri.platform.dto.AiRagResponse;
import com.agri.platform.dto.RagDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRagService {

    private final VectorSearchService vectorSearchService;
    private final AiService aiService;

    public AiRagResponse chatWithRag(AiRagRequest request, String userRole) {
        if (request.getUseRag() == null || !request.getUseRag()) {
            // RAG disabled, use normal chat
            String reply = aiService.chat(request.getMessage(), convertMessages(request.getMessages()));
            return new AiRagResponse(reply, List.of(), false);
        }

        try {
            // 1. Search for relevant documents
            List<RagDocument> sources = vectorSearchService.search(
                request.getMessage(),
                userRole,
                3 // top 3 results
            );

            // 2. Build context from sources
            String context = buildContext(sources);

            // 3. Generate AI response with context
            String reply = aiService.chatWithContext(
                request.getMessage(),
                convertMessages(request.getMessages()),
                context
            );

            return new AiRagResponse(reply, sources, true);

        } catch (Exception e) {
            log.error("RAG chat failed, falling back to normal chat", e);
            // Fallback to normal chat
            String reply = aiService.chat(request.getMessage(), convertMessages(request.getMessages()));
            return new AiRagResponse(reply, List.of(), false);
        }
    }

    private String buildContext(List<RagDocument> sources) {
        if (sources.isEmpty()) {
            return "暂无相关参考信息。";
        }

        return sources.stream()
            .map(doc -> String.format(
                "【参考】%s (相似度: %.2f)\n%s",
                doc.getTitle(),
                doc.getSimilarity(),
                doc.getContent()
            ))
            .collect(Collectors.joining("\n\n"));
    }

    private List<AiService.ChatMessage> convertMessages(List<AiRagRequest.ChatMessage> messages) {
        if (messages == null) return List.of();

        return messages.stream()
            .map(msg -> new AiService.ChatMessage(msg.getRole(), msg.getContent()))
            .collect(Collectors.toList());
    }
}
