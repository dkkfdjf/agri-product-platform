package com.agri.platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiRagRequest {
    private String message;
    private Boolean useRag = true;
    private List<ChatMessage> messages;

    @Data
    public static class ChatMessage {
        private String role;
        private String content;
    }
}
