-- Vector Index Table
CREATE TABLE IF NOT EXISTS tb_vector_index (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doc_type VARCHAR(20) NOT NULL COMMENT '文档类型: knowledge/question',
    doc_id INT NOT NULL COMMENT '对应文档ID',
    chunk_index INT NOT NULL COMMENT '切片索引',
    chunk_content TEXT NOT NULL COMMENT '切片内容',
    vector JSON NOT NULL COMMENT 'BGE-M3向量数组',
    content_hash VARCHAR(64) COMMENT '内容哈希，用于变化检测',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_doc (doc_type, doc_id),
    INDEX idx_hash (content_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量索引表';

-- Vector Task Table
CREATE TABLE IF NOT EXISTS tb_vector_task (
    id INT PRIMARY KEY AUTO_INCREMENT,
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型: rebuild/update/delete',
    doc_type VARCHAR(20) NOT NULL,
    doc_id INT NOT NULL,
    status TINYINT DEFAULT 0 COMMENT '0待处理 1处理中 2完成 3失败',
    error_msg VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_doc (doc_type, doc_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='向量化任务表';
