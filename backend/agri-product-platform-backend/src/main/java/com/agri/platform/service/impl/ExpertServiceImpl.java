package com.agri.platform.service.impl;

import com.agri.platform.dto.ExpertRequest;
import com.agri.platform.entity.Expert;
import com.agri.platform.entity.Reserve;
import com.agri.platform.mapper.ExpertMapper;
import com.agri.platform.mapper.ReserveMapper;
import com.agri.platform.service.ExpertService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpertServiceImpl implements ExpertService {

    @Autowired
    private ExpertMapper expertMapper;

    @Autowired
    private ReserveMapper reserveMapper;

    @Override
    public void updateExpert(String userName, ExpertRequest request) {
        Expert expert = expertMapper.selectById(userName);
        if (expert == null) {
            throw new RuntimeException("专家不存在");
        }
        if (request.getRealName() != null) expert.setRealName(request.getRealName());
        if (request.getPhone() != null) expert.setPhone(request.getPhone());
        if (request.getProfession() != null) expert.setProfession(request.getProfession());
        if (request.getPosition() != null) expert.setPosition(request.getPosition());
        if (request.getBelong() != null) expert.setBelong(request.getBelong());
        expertMapper.updateById(expert);
    }

    @Override
    public Expert getExpertByUserName(String userName) {
        return expertMapper.selectById(userName);
    }

    @Override
    public Page<Reserve> getReserveList(String expertName, int page, int pageSize) {
        Page<Reserve> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Reserve> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reserve::getExpertName, expertName);
        wrapper.orderByDesc(Reserve::getCreateTime);
        return reserveMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void confirmReserve(Integer reserveId, String expertName, Integer status) {
        Reserve reserve = reserveMapper.selectById(reserveId);
        if (reserve == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!reserve.getExpertName().equals(expertName)) {
            throw new RuntimeException("无权限处理此预约");
        }
        reserve.setStatus(status);
        reserveMapper.updateById(reserve);
    }

    @Override
    public List<Expert> getAllExperts() {
        return expertMapper.selectList(null);
    }
}
