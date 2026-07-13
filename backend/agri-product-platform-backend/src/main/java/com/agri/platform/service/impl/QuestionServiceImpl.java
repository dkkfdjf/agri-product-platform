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
        if (request.getExpertName() == null || request.getExpertName().isEmpty()) {
            throw new RuntimeException("请选择提问的专家");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new RuntimeException("请填写问题标题");
        }
        Question question = new Question();
        // expert_name 存储目标专家的 userName（与 tb_expert.user_name 对齐）
        question.setExpertName(request.getExpertName());
        question.setQuestioner(userName);
        question.setPhone(request.getPhone());
        question.setPlantName(request.getPlantName());
        question.setTitle(request.getTitle());
        question.setQuestion(request.getQuestion());
        question.setStatus(0); // 未回答
        questionMapper.insert(question);
    }

    @Override
    public Page<Question> getQuestionList(int page, int pageSize) {
        return getQuestionList(page, pageSize, null, null, null);
    }

    @Override
    public Page<Question> getQuestionList(int page, int pageSize, Integer status, String questioner, String expertName) {
        Page<Question> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Question::getStatus, status);
        }
        if (questioner != null && !questioner.isEmpty()) {
            wrapper.eq(Question::getQuestioner, questioner);
        }
        if (expertName != null && !expertName.isEmpty()) {
            // expert_name 存储的是专家 userName，使用精确匹配
            wrapper.eq(Question::getExpertName, expertName);
        }
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

    @Override
    public void deleteQuestion(Integer questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new RuntimeException("问题不存在");
        }
        questionMapper.deleteById(questionId);
    }
}
