package com.agri.platform.service;

import com.agri.platform.dto.QuestionRequest;
import com.agri.platform.entity.Question;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface QuestionService {
    void askQuestion(String userName, QuestionRequest request);
    Page<Question> getQuestionList(int page, int pageSize);
    Page<Question> getQuestionList(int page, int pageSize, Integer status, String questioner, String expertName);
    Question getQuestionById(Integer questionId);
    void answerQuestion(Integer questionId, String userName, String answer);
    void closeQuestion(Integer questionId, String userName);
}
