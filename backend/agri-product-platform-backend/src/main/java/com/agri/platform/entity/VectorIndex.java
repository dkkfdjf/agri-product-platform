package com.agri.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("tb_vector_index")
public class VectorIndex {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String docType;      // "knowledge" | "question"
    private Integer docId;       // 对应文档ID
    private Integer chunkIndex;  // 切片索引
    private String chunkContent; // 切片内容
    private String vector;       // JSON格式的向量数组
    private String contentHash;  // 内容哈希
    private LocalDateTime updateTime;
}
