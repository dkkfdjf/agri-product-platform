package com.agri.platform.service.impl;

import com.agri.platform.dto.ReserveRequest;
import com.agri.platform.entity.Expert;
import com.agri.platform.entity.Reserve;
import com.agri.platform.entity.User;
import com.agri.platform.mapper.ExpertMapper;
import com.agri.platform.mapper.ReserveMapper;
import com.agri.platform.mapper.UserMapper;
import com.agri.platform.service.MessageService;
import com.agri.platform.service.ReserveService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReserveServiceImpl implements ReserveService {

    @Autowired
    private ReserveMapper reserveMapper;

    @Autowired
    private ExpertMapper expertMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageService messageService;

    @Override
    public void makeReservation(String userName, ReserveRequest request) {
        // expert_name 存储目标专家的 userName
        String expertName = request.getExpertName() != null ? request.getExpertName() : request.getExpertId();
        if (expertName == null || expertName.isEmpty()) {
            throw new RuntimeException("请选择要预约的专家");
        }
        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            throw new RuntimeException("请填写联系电话");
        }
        if (request.getAddress() == null || request.getAddress().isEmpty()) {
            throw new RuntimeException("请填写地址");
        }
        if (request.getPlantName() == null || request.getPlantName().isEmpty()) {
            throw new RuntimeException("请填写农作物");
        }
        if (request.getSoilCondition() == null || request.getSoilCondition().isEmpty()) {
            throw new RuntimeException("请填写土壤条件");
        }
        if (request.getPlantCondition() == null || request.getPlantCondition().isEmpty()) {
            throw new RuntimeException("请填写作物条件");
        }
        if (request.getPlantDetail() == null || request.getPlantDetail().isEmpty()) {
            throw new RuntimeException("请填写作物详情");
        }

        Reserve reserve = new Reserve();
        reserve.setExpertName(expertName);
        reserve.setQuestioner(userName);
        reserve.setPhone(request.getPhone());
        reserve.setAddress(request.getAddress());
        reserve.setArea(request.getArea());
        reserve.setPlantName(request.getPlantName());
        reserve.setSoilCondition(request.getSoilCondition());
        reserve.setPlantCondition(request.getPlantCondition());
        reserve.setPlantDetail(request.getPlantDetail());
        reserve.setMessage(request.getMessage());
        reserve.setPreferredTime(request.getPreferredTime());
        reserve.setStatus(0); // 待确认
        // 冗余真实姓名，避免显示登录账号
        User asker = userMapper.selectById(userName);
        if (asker != null) {
            reserve.setQuestionerRealName(asker.getRealName());
        }
        User expertUser = userMapper.selectById(expertName);
        if (expertUser != null) {
            reserve.setExpertRealName(expertUser.getRealName());
        }
        reserveMapper.insert(reserve);

        // 通知被预约的专家：收到新预约申请
        String askerDisplay = asker != null && asker.getRealName() != null ? asker.getRealName() : userName;
        String plant = request.getPlantName();
        messageService.send(expertName, "reserve",
                "您有新的预约申请",
                askerDisplay + " 预约了您" + (plant != null && !plant.isEmpty() ? "（" + plant + "）" : ""),
                "/expert/reservations");
    }

    @Override
    public Page<Reserve> getReserveList(String userName, int page, int pageSize) {
        Page<Reserve> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Reserve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reserve::getQuestioner, userName);
        wrapper.orderByDesc(Reserve::getCreateTime);
        return reserveMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Page<Reserve> getReserveListByExpert(String expertName, int page, int pageSize) {
        Page<Reserve> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Reserve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reserve::getExpertName, expertName);
        wrapper.orderByDesc(Reserve::getCreateTime);
        return reserveMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void cancelReserve(Integer reserveId, String userName) {
        Reserve reserve = reserveMapper.selectById(reserveId);
        if (reserve == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!reserve.getQuestioner().equals(userName)) {
            throw new RuntimeException("无权限取消此预约");
        }
        reserveMapper.deleteById(reserveId);
    }

    @Override
    public void confirmReserve(Integer reserveId, String expertName, Integer status) {
        confirmReserve(reserveId, expertName, status, null);
    }

    @Override
    public void confirmReserve(Integer reserveId, String expertName, Integer status, String answer) {
        Reserve reserve = reserveMapper.selectById(reserveId);
        if (reserve == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!reserve.getExpertName().equals(expertName)) {
            throw new RuntimeException("无权限处理此预约");
        }
        reserve.setStatus(status);
        if (answer != null && !answer.isEmpty()) {
            reserve.setAnswer(answer);
        }
        // 冗余专家真实姓名
        if (reserve.getExpertRealName() == null) {
            User expertUser = userMapper.selectById(expertName);
            if (expertUser != null) {
                reserve.setExpertRealName(expertUser.getRealName());
            }
        }
        reserveMapper.updateById(reserve);

        // 通知预约人处理结果（1已确认 2已拒绝）
        String expertDisplay = reserve.getExpertRealName() != null ? reserve.getExpertRealName() : expertName;
        String title;
        String content;
        if (status != null && status == 1) {
            title = "预约已确认";
            content = "专家 " + expertDisplay + " 已确认您的预约。";
        } else if (status != null && status == 2) {
            title = "预约未通过";
            content = "专家 " + expertDisplay + " 暂时无法接受您的预约。";
        } else {
            title = "预约状态更新";
            content = "您的预约状态已更新。";
        }
        messageService.send(reserve.getQuestioner(), "reserve", title, content, null);
    }
}
