package com.agri.platform.dto;

import lombok.Data;

@Data
public class DocumentChunk {
    private String docType;        // "knowledge" | "question"
    private Integer docId;         // 原文档 ID
    private Integer chunkIndex;    // 切片索引
    private String content;        // 切片内容
    private String contentHash;    // 内容哈希

    public DocumentChunk() {}

    public DocumentChunk(String docType, Integer docId, Integer chunkIndex, String content, String contentHash) {
        this.docType = docType;
        this.docId = docId;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.contentHash = contentHash;
    }
}
