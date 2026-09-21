package com.agri.platform.dto;

import lombok.Data;
import java.util.List;

@Data
public class AiRagResponse {
    private String reply;
    private List<RagDocument> sources;
    private Boolean usedRag;

    public AiRagResponse() {}

    public AiRagResponse(String reply, List<RagDocument> sources, Boolean usedRag) {
        this.reply = reply;
        this.sources = sources;
        this.usedRag = usedRag;
    }
}
