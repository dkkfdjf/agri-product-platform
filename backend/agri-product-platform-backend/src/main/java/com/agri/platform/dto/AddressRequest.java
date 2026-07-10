package com.agri.platform.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private Integer id;
    private String consignee;
    private String phone;
    private String addressDetail;
    private Integer isDefault;
}