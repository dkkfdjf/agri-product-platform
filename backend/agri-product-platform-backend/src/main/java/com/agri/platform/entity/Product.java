package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("tb_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    private String title;
    private BigDecimal price;
    private String content;
    /** 图片（API/JSON 字段统一为 picPath，@TableField 映射到 DB 列 picture） */
    @TableField("picture")
    private String picPath;
    private String type; // goods货源/demand需求
    private Integer orderStatus; // 0待交易 1交易中 2已完成
    private String ownName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}