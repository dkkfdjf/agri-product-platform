package com.agri.platform.service;

import com.agri.platform.dto.RagDocument;
import com.agri.platform.entity.VectorIndex;
import com.agri.platform.mapper.VectorIndexMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorSearchService {

    private final VectorIndexMapper vectorIndexMapper;
    private final BgeM3EmbeddingService embeddingService;

    private static final double MIN_SIMILARITY = 0.6;

    public List<RagDocument> search(String query, String userRole, int topK) {
        try {
            // 1. Embed the query
            double[] queryVector = embeddingService.embed(query);
            if (queryVector.length == 0) {
                log.warn("Failed to embed query");
                return new ArrayList<>();
            }

            // 2. Load all vectors from database
            List<VectorIndex> allVectors = vectorIndexMapper.findAll();

            // 3. Calculate similarities
            List<RagDocument> results = new ArrayList<>();
            for (VectorIndex vectorIndex : allVectors) {
                double[] docVector = parseVectorJson(vectorIndex.getVector());
                if (docVector.length == queryVector.length) {
                    double similarity = cosineSimilarity(queryVector, docVector);
                    if (similarity >= MIN_SIMILARITY) {
                        results.add(new RagDocument(
                            vectorIndex.getDocType(),
                            vectorIndex.getDocId(),
                            "Document " + vectorIndex.getDocId(),
                            vectorIndex.getChunkContent(),
                            similarity
                        ));
                    }
                }
            }

            // 4. Sort by similarity and filter by permission
            List<RagDocument> filtered = filterByPermission(results, userRole);
            filtered.sort(Comparator.comparing(RagDocument::getSimilarity, Comparator.reverseOrder()));

            // 5. Return top K results
            return filtered.stream()
                .limit(topK)
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Vector search failed", e);
            return new ArrayList<>();
        }
    }

    private double[] parseVectorJson(String vectorJson) {
        try {
            // Parse JSON array like [0.1, -0.2, 0.3, ...]
            String cleaned = vectorJson.replaceAll("[\\[\\]\"]", "").trim();
            if (cleaned.isEmpty()) {
                return new double[0];
            }

            String[] parts = cleaned.split(",");
            double[] vector = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vector[i] = Double.parseDouble(parts[i].trim());
            }
            return vector;
        } catch (Exception e) {
            log.error("Failed to parse vector JSON: {}", vectorJson, e);
            return new double[0];
        }
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private List<RagDocument> filterByPermission(List<RagDocument> results, String userRole) {
        if (canViewUnpublished(userRole)) {
            return results; // Experts and admins see all
        }

        // Regular users only see published content (implementation depends on your business logic)
        // For now, return all as we don't have status info in VectorIndex
        return results;
    }

    private boolean canViewUnpublished(String userRole) {
        return "expert".equals(userRole) || "admin".equals(userRole);
    }
}
