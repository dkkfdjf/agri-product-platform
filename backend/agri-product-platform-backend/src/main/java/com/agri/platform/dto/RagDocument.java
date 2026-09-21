package com.agri.platform.dto;

import lombok.Data;

@Data
public class RagDocument {
    private String type;         // "knowledge" | "question"
    private Integer id;          // 文档 ID
    private String title;        // 标题
    private String content;      // 内容摘要
    private Double similarity;   // 相似度分数 (0-1)

    public RagDocument() {}

    public RagDocument(String type, Integer id, String title, String content, Double similarity) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.content = content;
        this.similarity = similarity;
    }
}
