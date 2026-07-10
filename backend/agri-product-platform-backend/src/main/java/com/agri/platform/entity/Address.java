package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_address")
public class Address {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String ownName;
    private String consignee;
    private String phone;
    private String addressDetail;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}