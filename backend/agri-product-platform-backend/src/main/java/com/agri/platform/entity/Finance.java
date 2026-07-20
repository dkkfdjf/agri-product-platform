package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_finance")
public class Finance {
    @TableId(type = IdType.AUTO)
    private Integer financeId;
    private Integer productId;
    private String ownName;
    private String realName;
    private String phone;
    private String idNum;
    private BigDecimal money;
    private BigDecimal rate;
    private Integer repayment;
    private Integer status; // 0申请中 1已通过 2已驳回
    private String remark;
    private String purpose;          // 申请原因/融资用途
    private String repaymentSource;  // 还款来源
    private String combinationName1;
    private String combinationPhone1;
    private String combinationIdnum1;
    private String combinationName2;
    private String combinationPhone2;
    private String combinationIdnum2;
    private String fileInfo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    /**
     * 非数据库字段：申请所对应的融资产品（银行）名称，仅在银行工作台列表中回填展示。
     * 不参与持久化，对其它接口无副作用（默认为 null）。
     */
    @TableField(exist = false)
    private String productName;
}