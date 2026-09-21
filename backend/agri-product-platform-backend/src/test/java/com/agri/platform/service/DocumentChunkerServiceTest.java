package com.agri.platform.service;

import com.agri.platform.dto.DocumentChunk;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DocumentChunkerServiceTest {

    @Test
    void testChunkDocument() {
        DocumentChunkerService service = new DocumentChunkerService();
        String content = "A".repeat(1500); // 1500 characters
        List<DocumentChunk> chunks = service.chunkDocument(content, "knowledge", 1);

        assertEquals(3, chunks.size()); // Should be 3 chunks
        assertEquals(500, chunks.get(0).getContent().length());
        assertEquals(500, chunks.get(1).getContent().length());
        assertEquals(500, chunks.get(2).getContent().length());
    }

    @Test
    void testChunkLessThanSize() {
        DocumentChunkerService service = new DocumentChunkerService();
        String content = "AB";
        List<DocumentChunk> chunks = service.chunkDocument(content, "knowledge", 1);

        assertEquals(1, chunks.size());
        assertEquals(2, chunks.get(0).getContent().length());
    }

    @Test
    void testContentHashGeneration() {
        DocumentChunkerService service = new DocumentChunkerService();
        String content = "test content";
        List<DocumentChunk> chunks = service.chunkDocument(content, "knowledge", 1);

        assertNotNull(chunks.get(0).getContentHash());
        assertFalse(chunks.get(0).getContentHash().isEmpty());
    }
}
