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
        Expert expert = expertMapper.selectById(request.getExpertId());
        Reserve reserve = new Reserve();
        reserve.setExpertName(expert != null ? expert.getRealName() : request.getExpertId());
        reserve.setQuestioner(userName);
        reserve.setMessage(request.getReason());
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
}
