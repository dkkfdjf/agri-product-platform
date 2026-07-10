package com.agri.platform.service.impl;

import com.agri.platform.dto.QuestionRequest;
import com.agri.platform.entity.Question;
import com.agri.platform.mapper.QuestionMapper;
import com.agri.platform.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public void askQuestion(String userName, QuestionRequest request) {
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setQuestion(request.getContent());
        question.setQuestioner(userName);
        question.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        questionMapper.insert(question);
    }

    @Override
    public Page<Question> getQuestionList(int page, int pageSize) {
        Page<Question> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Question::getCreateTime);
        return questionMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Question getQuestionById(Integer questionId) {
        return questionMapper.selectById(questionId);
    }

    @Override
    public void answerQuestion(Integer questionId, String userName, String answer) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        question.setAnswer(answer);
        question.setExpertName(userName);
        question.setStatus(1); // 已回答
        questionMapper.updateById(question);
    }

    @Override
    public void closeQuestion(Integer questionId, String userName) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        if (!question.getQuestioner().equals(userName)) {
            throw new RuntimeException("无权限关闭此问题");
        }
        question.setStatus(2); // 已关闭
        questionMapper.updateById(question);
    }
}
