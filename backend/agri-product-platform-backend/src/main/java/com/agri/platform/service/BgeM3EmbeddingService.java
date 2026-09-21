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
public class BgeM3EmbeddingService {

    private final TencentMaaProperties properties;
    /** 读取超时 tencent.maas.timeout（默认30s），由 WebConfig#aiRestTemplate 提供 */
    private final RestTemplate aiRestTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BgeM3EmbeddingService(TencentMaaProperties properties,
                                 @Qualifier("aiRestTemplate") RestTemplate aiRestTemplate) {
        this.properties = properties;
        this.aiRestTemplate = aiRestTemplate;
    }

    /**
     * Single text embedding
     */
    public double[] embed(String text) {
        List<double[]> embeddings = embedBatch(List.of(text));
        return embeddings.isEmpty() ? new double[0] : embeddings.get(0);
    }

    /**
     * Batch text embedding (max 10 texts per batch)
     */
    public List<double[]> embedBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return new ArrayList<>();
        }

        // Limit batch size
        if (texts.size() > 10) {
            log.warn("Batch size exceeds 10, splitting into multiple calls");
            return processLargeBatch(texts);
        }

        try {
            // Build request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", properties.getEmbeddingModel());
            requestBody.put("input", texts);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(properties.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.debug("Calling BGE-M3 API for {} texts", texts.size());

            // Call API
            ResponseEntity<String> response = aiRestTemplate.exchange(
                properties.getEmbeddingUrl(),
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return parseEmbeddingResponse(response.getBody());
            }

            log.error("BGE-M3 API returned non-OK status: {}", response.getStatusCode());
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("Failed to call BGE-M3 API", e);
            return new ArrayList<>();
        }
    }

    private List<double[]> parseEmbeddingResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode data = root.path("data");

            List<double[]> embeddings = new ArrayList<>();
            if (data.isArray()) {
                for (JsonNode item : data) {
                    JsonNode embeddingArray = item.path("embedding");
                    double[] vector = new double[embeddingArray.size()];
                    for (int i = 0; i < embeddingArray.size(); i++) {
                        vector[i] = embeddingArray.get(i).asDouble();
                    }
                    embeddings.add(vector);
                }
            }

            return embeddings;
        } catch (Exception e) {
            log.error("Failed to parse embedding response", e);
            return new ArrayList<>();
        }
    }

    private List<double[]> processLargeBatch(List<String> texts) {
        List<double[]> allEmbeddings = new ArrayList<>();
        int batchSize = 10;

        for (int i = 0; i < texts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, end);
            allEmbeddings.addAll(embedBatch(batch));
        }

        return allEmbeddings;
    }
}
