package com.agri.platform.service;

import com.agri.platform.dto.DocumentChunk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class DocumentChunkerService {

    private static final int CHUNK_SIZE = 500;

    public List<DocumentChunk> chunkDocument(String content, String docType, Integer docId) {
        List<DocumentChunk> chunks = new ArrayList<>();

        if (content == null || content.isEmpty()) {
            return chunks;
        }

        int totalLength = content.length();
        int chunkCount = (int) Math.ceil((double) totalLength / CHUNK_SIZE);

        for (int i = 0; i < chunkCount; i++) {
            int start = i * CHUNK_SIZE;
            int end = Math.min(start + CHUNK_SIZE, totalLength);
            String chunkContent = content.substring(start, end);

            chunks.add(new DocumentChunk(
                docType,
                docId,
                i,
                chunkContent,
                generateContentHash(chunkContent)
            ));
        }

        log.debug("Chunked document {}:{} into {} chunks", docType, docId, chunks.size());
        return chunks;
    }

    private String generateContentHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to generate content hash", e);
            return String.valueOf(content.hashCode());
        }
    }
}
