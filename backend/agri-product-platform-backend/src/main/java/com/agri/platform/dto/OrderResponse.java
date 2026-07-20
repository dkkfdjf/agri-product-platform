package com.agri.platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应 DTO
 */
@Data
public class OrderResponse {
    private Integer purchaseId;

    /** 兼容旧前端：同时以 id 暴露订单ID（如 farmer/Orders 用 order.orderId||order.id） */
    @JsonProperty("id")
    public Integer getId() {
        return purchaseId;
    }

    private String ownName;
    private BigDecimal totalPrice;
    private String address;
    private Integer purchaseStatus;
    private String statusText;
    private Integer purchaseType;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private List<OrderItemResponse> items;

    @Data
    public static class OrderItemResponse {
        private Integer productId;
        private String productName;
        private String productPic;
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice;
    }
}
