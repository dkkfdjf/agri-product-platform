package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 还款计划（等额本息）。融资审批通过后自动生成，农户逐期还款。
 */
@Data
@TableName("tb_repayment")
public class Repayment {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer financeId;
    private Integer periodIndex;      // 期数 1..N
    private LocalDate dueDate;        // 到期日
    private BigDecimal principal;     // 本期本金
    private BigDecimal interest;      // 本期利息
    private BigDecimal totalAmount;   // 本期应还本息
    private BigDecimal paidAmount;    // 已还金额
    private Integer status;           // 0未还 1已还（逾期在读取时动态判定）
    private LocalDateTime paidTime;
    private LocalDateTime createTime;

    /** 非数据库字段：是否逾期（status==0 且到期日早于今天），仅在列表读取时回填 */
    @TableField(exist = false)
    private Boolean overdue;
}
