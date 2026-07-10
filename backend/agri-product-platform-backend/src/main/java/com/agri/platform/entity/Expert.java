package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_expert")
public class Expert {
    private String userName;
    private String realName;
    private String phone;
    private String profession;
    private String position;
    private String belong;
}