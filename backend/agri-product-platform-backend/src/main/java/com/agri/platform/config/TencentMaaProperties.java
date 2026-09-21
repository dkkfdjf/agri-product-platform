package com.agri.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tencent.maas")
public class TencentMaaProperties {
    private String apiKey;
    private String apiUrl;
    private String model;
    private String embeddingUrl;
    private String embeddingModel;
    private Integer timeout = 30000;
}
