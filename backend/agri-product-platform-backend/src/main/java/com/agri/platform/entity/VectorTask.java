package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_vector_task")
public class VectorTask {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String taskType;     // "rebuild" | "update" | "delete"
    private String docType;
    private Integer docId;
    private Integer status;      // 0待处理 1处理中 2完成 3失败
    private String errorMsg;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
