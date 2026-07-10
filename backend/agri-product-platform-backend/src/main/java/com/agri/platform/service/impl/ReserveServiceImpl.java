package com.agri.platform.service.impl;

import com.agri.platform.dto.ReserveRequest;
import com.agri.platform.entity.Expert;
import com.agri.platform.entity.Reserve;
import com.agri.platform.mapper.ExpertMapper;
import com.agri.platform.mapper.ReserveMapper;
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

    @Override
    public void makeReservation(String userName, ReserveRequest request) {
        Reserve reserve = new Reserve();
        // 使用传入的expertName而不是查找expert表
        reserve.setExpertName(request.getExpertName() != null ? request.getExpertName() : request.getExpertId());
        reserve.setQuestioner(userName);
        reserve.setPhone(request.getPhone());
        reserve.setAddress(request.getAddress());
        reserve.setArea(request.getArea());
        reserve.setPlantName(request.getPlantName());
        reserve.setSoilCondition(request.getSoilCondition());
        reserve.setPlantCondition(request.getPlantCondition());
        reserve.setPlantDetail(request.getPlantDetail());
        reserve.setMessage(request.getMessage());
        reserve.setStatus(0); // 待确认
        reserveMapper.insert(reserve);
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
        reserveMapper.updateById(reserve);
    }
}
