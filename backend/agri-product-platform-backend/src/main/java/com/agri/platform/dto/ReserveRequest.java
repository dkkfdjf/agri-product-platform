package com.agri.platform.dto;

import lombok.Data;

@Data
public class ReserveRequest {
    private String expertId;
    private String time;
    private String reason;
}
