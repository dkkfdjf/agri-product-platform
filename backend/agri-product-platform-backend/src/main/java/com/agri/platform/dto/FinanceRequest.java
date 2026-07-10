package com.agri.platform.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FinanceRequest {
    private Integer financeId;
    private Integer productId;
    private String realName;
    private String phone;
    private String idNum;
    private BigDecimal money;
    private Integer repayment;
    private String combinationName1;
    private String combinationPhone1;
    private String combinationIdnum1;
    private String combinationName2;
    private String combinationPhone2;
    private String combinationIdnum2;
    private String fileInfo;
}