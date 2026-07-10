package com.agri.platform.dto;

import lombok.Data;

@Data
public class QuestionRequest {
    private Integer questionId;
    private String title;
    private String content;
    private Integer status; // 0待回答 1已回答 2已关闭
}
