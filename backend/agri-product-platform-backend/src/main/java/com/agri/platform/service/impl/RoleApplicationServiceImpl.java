package com.agri.platform.service.impl;

import com.agri.platform.dto.RoleApplicationRequest;
import com.agri.platform.entity.Expert;
import com.agri.platform.entity.RoleApplication;
import com.agri.platform.entity.User;
import com.agri.platform.mapper.ExpertMapper;
import com.agri.platform.mapper.RoleApplicationMapper;
import com.agri.platform.mapper.UserMapper;
import com.agri.platform.service.RoleApplicationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleApplicationServiceImpl implements RoleApplicationService {

    @Autowired
    private RoleApplicationMapper roleApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ExpertMapper expertMapper;

    @Override
    public void apply(String userName, RoleApplicationRequest request) {
        User user = userMapper.selectById(userName);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 仅 农户/买家 可申请升级角色
        if (!"farmer".equals(user.getRole()) && !"buyer".equals(user.getRole())) {
            throw new RuntimeException("当前角色无需申请");
        }
        String target = request.getTargetRole();
        if (!"expert".equals(target) && !"bank".equals(target)) {
            throw new RuntimeException("仅支持申请成为专家或银行角色");
        }
        // 不允许重复提交未处理的申请
        Long pending = roleApplicationMapper.selectCount(
                new LambdaQueryWrapper<RoleApplication>()
                        .eq(RoleApplication::getUserName, userName)
                        .eq(RoleApplication::getStatus, 0));
        if (pending != null && pending > 0) {
            throw new RuntimeException("您已有一条待审核的申请，请等待处理");
        }

        RoleApplication app = new RoleApplication();
        app.setUserName(userName);
        app.setTargetRole(target);
        app.setRealName(request.getRealName() != null ? request.getRealName() : user.getRealName());
        app.setPhone(request.getPhone() != null ? request.getPhone() : user.getPhone());
        app.setProfession(request.getProfession());
        app.setPosition(request.getPosition());
        app.setBelong(request.getBelong());
        app.setReason(request.getReason());
        app.setStatus(0);
        roleApplicationMapper.insert(app);
    }

    @Override
    public List<RoleApplication> listMine(String userName) {
        return roleApplicationMapper.selectList(
                new LambdaQueryWrapper<RoleApplication>()
                        .eq(RoleApplication::getUserName, userName)
                        .orderByDesc(RoleApplication::getCreateTime));
    }

    @Override
    public List<RoleApplication> listAll(Integer status) {
        LambdaQueryWrapper<RoleApplication> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(RoleApplication::getStatus, status);
        }
        wrapper.orderByDesc(RoleApplication::getCreateTime);
        return roleApplicationMapper.selectList(wrapper);
    }

    @Override
    public void review(Integer id, String reviewer, Integer status, String reviewRemark) {
        RoleApplication app = roleApplicationMapper.selectById(id);
        if (app == null) {
            throw new RuntimeException("申请不存在");
        }
        if (app.getStatus() != 0) {
            throw new RuntimeException("该申请已处理");
        }
        if (status == null || (status != 1 && status != 2)) {
            throw new RuntimeException("审核状态非法");
        }

        app.setStatus(status);
        app.setReviewer(reviewer);
        app.setReviewRemark(reviewRemark);
        roleApplicationMapper.updateById(app);

        // 通过：变更用户角色；专家需建立资料
        if (status == 1) {
            User user = userMapper.selectById(app.getUserName());
            if (user == null) {
                throw new RuntimeException("申请人不存在");
            }
            user.setRole(app.getTargetRole());
            user.setRealName(app.getRealName() != null ? app.getRealName() : user.getRealName());
            user.setPhone(app.getPhone() != null ? app.getPhone() : user.getPhone());
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);

            if ("expert".equals(app.getTargetRole())) {
                // 已存在则更新，否则新建
                Expert expert = expertMapper.selectById(app.getUserName());
                if (expert == null) {
                    expert = new Expert();
                    expert.setUserName(app.getUserName());
                    expert.setRealName(user.getRealName());
                    expert.setPhone(user.getPhone());
                    expert.setProfession(app.getProfession());
                    expert.setPosition(app.getPosition());
                    expert.setBelong(app.getBelong());
                    expertMapper.insert(expert);
                } else {
                    expert.setProfession(app.getProfession());
                    expert.setPosition(app.getPosition());
                    expert.setBelong(app.getBelong());
                    expertMapper.updateById(expert);
                }
            }
        }
    }
}
