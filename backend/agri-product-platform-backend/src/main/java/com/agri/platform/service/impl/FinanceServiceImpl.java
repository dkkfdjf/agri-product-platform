package com.agri.platform.service.impl;

import com.agri.platform.dto.FinanceApprovalRequest;
import com.agri.platform.dto.FinanceProductRequest;
import com.agri.platform.dto.FinanceRequest;
import com.agri.platform.entity.Finance;
import com.agri.platform.entity.FinanceProduct;
import com.agri.platform.mapper.FinanceMapper;
import com.agri.platform.mapper.FinanceProductMapper;
import com.agri.platform.mapper.UserMapper;
import com.agri.platform.service.FinanceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinanceServiceImpl implements FinanceService {

    @Autowired
    private FinanceProductMapper productMapper;

    @Autowired
    private FinanceMapper financeMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void publishProduct(String userName, FinanceProductRequest request) {
        // 验证用户是银行角色
        if (!userName.startsWith("bank")) {
            throw new RuntimeException("只有银行可以发布融资产品");
        }

        FinanceProduct product = new FinanceProduct();
        product.setBankName(request.getBankName());
        product.setIntroduce(request.getIntroduce());
        product.setBankPhone(request.getBankPhone());
        product.setMoney(request.getMoney());
        product.setRate(request.getRate());
        product.setRepayment(request.getRepayment());
        productMapper.insert(product);
    }

    @Override
    public Page<FinanceProduct> getProductList(int page, int pageSize) {
        Page<FinanceProduct> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<FinanceProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(FinanceProduct::getCreateTime);
        return productMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public FinanceProduct getProductById(Integer productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public void applyFinance(String userName, FinanceRequest request) {
        // 验证用户是农户角色
        if (!userName.startsWith("farmer")) {
            throw new RuntimeException("只有农户可以申请融资");
        }

        // 获取融资产品信息
        FinanceProduct product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("融资产品不存在");
        }

        Finance finance = new Finance();
        finance.setProductId(request.getProductId());
        finance.setOwnName(userName);
        finance.setRealName(request.getRealName());
        finance.setPhone(request.getPhone());
        finance.setIdNum(request.getIdNum());
        finance.setMoney(request.getMoney());
        finance.setRate(product.getRate());
        finance.setRepayment(product.getRepayment());
        finance.setStatus(0); // 申请中
        finance.setCombinationName1(request.getCombinationName1());
        finance.setCombinationPhone1(request.getCombinationPhone1());
        finance.setCombinationIdnum1(request.getCombinationIdnum1());
        finance.setCombinationName2(request.getCombinationName2());
        finance.setCombinationPhone2(request.getCombinationPhone2());
        finance.setCombinationIdnum2(request.getCombinationIdnum2());
        finance.setFileInfo(request.getFileInfo());
        financeMapper.insert(finance);
    }

    @Override
    public Page<Finance> getApplyList(String userName, int page, int pageSize) {
        Page<Finance> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Finance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Finance::getOwnName, userName);
        wrapper.orderByDesc(Finance::getCreateTime);
        return financeMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Page<Finance> getBankApprovalList(int page, int pageSize) {
        Page<Finance> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Finance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Finance::getStatus, 0); // 待审批
        wrapper.orderByDesc(Finance::getCreateTime);
        return financeMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public void approveFinance(Integer financeId, FinanceApprovalRequest request) {
        Finance finance = financeMapper.selectById(financeId);
        if (finance == null) {
            throw new RuntimeException("融资申请不存在");
        }
        finance.setStatus(request.getStatus());
        finance.setRemark(request.getRemark());
        financeMapper.updateById(finance);
    }

    @Override
    public void updateFinance(Integer financeId, String userName, FinanceRequest request) {
        // 只有申请者可以修改（在审批前）
        Finance finance = financeMapper.selectById(financeId);
        if (finance == null) {
            throw new RuntimeException("融资申请不存在");
        }
        if (!finance.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限修改此申请");
        }
        if (finance.getStatus() != 0) {
            throw new RuntimeException("只有申请中的记录可以修改");
        }
        // 更新允许修改的字段
        if (request.getRealName() != null) finance.setRealName(request.getRealName());
        if (request.getPhone() != null) finance.setPhone(request.getPhone());
        if (request.getIdNum() != null) finance.setIdNum(request.getIdNum());
        if (request.getMoney() != null) finance.setMoney(request.getMoney());
        if (request.getCombinationName1() != null) finance.setCombinationName1(request.getCombinationName1());
        if (request.getCombinationPhone1() != null) finance.setCombinationPhone1(request.getCombinationPhone1());
        if (request.getCombinationIdnum1() != null) finance.setCombinationIdnum1(request.getCombinationIdnum1());
        if (request.getCombinationName2() != null) finance.setCombinationName2(request.getCombinationName2());
        if (request.getCombinationPhone2() != null) finance.setCombinationPhone2(request.getCombinationPhone2());
        if (request.getCombinationIdnum2() != null) finance.setCombinationIdnum2(request.getCombinationIdnum2());
        if (request.getFileInfo() != null) finance.setFileInfo(request.getFileInfo());
        financeMapper.updateById(finance);
    }
}