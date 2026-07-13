package com.agri.platform.service.impl;

import com.agri.platform.dto.KnowledgeRequest;
import com.agri.platform.entity.Discuss;
import com.agri.platform.entity.Knowledge;
import com.agri.platform.mapper.DiscussMapper;
import com.agri.platform.mapper.KnowledgeMapper;
import com.agri.platform.service.KnowledgeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Autowired
    private DiscussMapper discussMapper;

    @Override
    public void publishKnowledge(String userName, KnowledgeRequest request) {
        Knowledge knowledge = new Knowledge();
        knowledge.setTitle(request.getTitle());
        knowledge.setContent(request.getContent());
        knowledge.setPicPath(request.getPicPath());
        knowledge.setOwnName(userName);
        // 默认发布（1）；草稿需显式传 0
        knowledge.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        knowledgeMapper.insert(knowledge);
    }

    @Override
    public Page<Knowledge> getKnowledgeList(int page, int pageSize) {
        return getKnowledgeList(page, pageSize, 1, null);
    }

    @Override
    public Page<Knowledge> getKnowledgeList(int page, int pageSize, Integer status, String ownName) {
        Page<Knowledge> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Knowledge::getStatus, status);
        } else if (ownName == null || ownName.isEmpty()) {
            // 公共列表（未指定发布者）只展示已发布，避免泄露他人草稿
            wrapper.eq(Knowledge::getStatus, 1);
        }
        if (ownName != null && !ownName.isEmpty()) {
            wrapper.eq(Knowledge::getOwnName, ownName);
        }
        wrapper.orderByDesc(Knowledge::getCreateTime);
        return knowledgeMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Knowledge getKnowledgeById(Integer knowledgeId) {
        return knowledgeMapper.selectById(knowledgeId);
    }

    @Override
    public void updateKnowledge(Integer knowledgeId, String userName, KnowledgeRequest request) {
        Knowledge knowledge = knowledgeMapper.selectById(knowledgeId);
        if (knowledge == null) {
            throw new RuntimeException("知识不存在");
        }
        if (!knowledge.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限修改此知识");
        }
        if (request.getTitle() != null) knowledge.setTitle(request.getTitle());
        if (request.getContent() != null) knowledge.setContent(request.getContent());
        if (request.getPicPath() != null) knowledge.setPicPath(request.getPicPath());
        if (request.getStatus() != null) knowledge.setStatus(request.getStatus());
        knowledgeMapper.updateById(knowledge);
    }

    @Override
    public void deleteKnowledge(Integer knowledgeId, String userName) {
        Knowledge knowledge = knowledgeMapper.selectById(knowledgeId);
        if (knowledge == null) {
            throw new RuntimeException("知识不存在");
        }
        if (!knowledge.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限删除此知识");
        }
        knowledgeMapper.deleteById(knowledgeId);
    }

    @Override
    public void addDiscuss(Integer knowledgeId, String userName, String content) {
        Discuss discuss = new Discuss();
        discuss.setKnowledgeId(knowledgeId);
        discuss.setOwnName(userName);
        discuss.setContent(content);
        discussMapper.insert(discuss);
    }

    @Override
    public List<Discuss> getDiscussList(Integer knowledgeId) {
        return discussMapper.selectList(
            new LambdaQueryWrapper<Discuss>()
                .eq(Discuss::getKnowledgeId, knowledgeId)
                .orderByAsc(Discuss::getCreateTime)
        );
    }

    @Override
    public void deleteDiscuss(Integer discussId, String userName) {
        Discuss discuss = discussMapper.selectById(discussId);
        if (discuss == null) {
            throw new RuntimeException("评论不存在");
        }
        // 评论者本人或知识发布者可以删除
        if (!discuss.getOwnName().equals(userName)) {
            // 检查是否是知识发布者
            Knowledge knowledge = knowledgeMapper.selectById(discuss.getKnowledgeId());
            if (knowledge == null || !knowledge.getOwnName().equals(userName)) {
                throw new RuntimeException("无权限删除此评论");
            }
        }
        discussMapper.deleteById(discussId);
    }
}
