package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_finance_product")
public class FinanceProduct {
    @TableId(type = IdType.AUTO)
    private Integer productId;
    private String bankName;
    private String introduce;
    private String bankPhone;
    private BigDecimal money;
    private BigDecimal rate;
    private Integer repayment;
    private Integer status; // 0在售/可申请 1暂停供应（不可删除，只能暂停）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}