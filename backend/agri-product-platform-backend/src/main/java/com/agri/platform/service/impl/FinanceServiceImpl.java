package com.agri.platform.service.impl;

import com.agri.platform.dto.BankFinanceStats;
import com.agri.platform.dto.FinanceApprovalRequest;
import com.agri.platform.dto.FinanceProductRequest;
import com.agri.platform.dto.FinanceRequest;
import com.agri.platform.entity.Finance;
import com.agri.platform.entity.FinanceProduct;
import com.agri.platform.entity.User;
import com.agri.platform.mapper.FinanceMapper;
import com.agri.platform.mapper.FinanceProductMapper;
import com.agri.platform.mapper.UserMapper;
import com.agri.platform.service.FinanceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        // 验证用户是银行角色（按 role 字段判断，不能用用户名前缀判断）
        User user = userMapper.selectById(userName);
        if (user == null || !"bank".equals(user.getRole())) {
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
        // 验证用户是农户角色（按 role 字段判断，不能用用户名前缀判断）
        User user = userMapper.selectById(userName);
        if (user == null || !"farmer".equals(user.getRole())) {
            throw new RuntimeException("只有农户可以申请融资");
        }

        // 申请必须使用该账号「注册时」的真实姓名和手机号：以后端查到的用户信息为准，
        // 忽略前端传入，避免被篡改或填错
        String realName = user.getRealName();
        String phone = user.getPhone();
        if (realName == null || realName.trim().isEmpty()) {
            throw new RuntimeException("请先在个人中心完善真实姓名后再申请融资");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("请先在个人中心完善手机号后再申请融资");
        }

        // 获取融资产品信息
        FinanceProduct product = productMapper.selectById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("融资产品不存在");
        }

        // 校验申请金额（money 为数据库 NOT NULL 列，必须校验，避免直接抛出原始 SQL 异常）
        if (request.getMoney() == null || request.getMoney().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("请输入正确的申请金额");
        }

        // 申请原因 / 还款来源：去首尾空格后不少于 15 个字
        String purpose = request.getPurpose() == null ? "" : request.getPurpose().trim();
        if (purpose.length() < 15) {
            throw new RuntimeException("申请原因不能少于15个字");
        }
        String repaymentSource = request.getRepaymentSource() == null ? "" : request.getRepaymentSource().trim();
        if (repaymentSource.length() < 15) {
            throw new RuntimeException("还款来源不能少于15个字");
        }

        Finance finance = new Finance();
        finance.setProductId(request.getProductId());
        finance.setOwnName(userName);
        finance.setRealName(realName.trim());    // 使用注册真实姓名
        finance.setPhone(phone.trim());          // 使用注册手机号
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
        finance.setPurpose(purpose);
        finance.setRepaymentSource(repaymentSource);
        financeMapper.insert(finance);
    }

    @Override
    public Page<Finance> getApplyList(String userName, int page, int pageSize) {
        Page<Finance> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Finance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Finance::getOwnName, userName);
        wrapper.orderByDesc(Finance::getCreateTime);
        Page<Finance> result = financeMapper.selectPage(pageParam, wrapper);
        // 回填产品（银行）名称，便于农户在「我的融资」中看到申请的是哪个产品
        fillProductName(result.getRecords());
        return result;
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

    @Override
    public void deleteFinance(Integer financeId, String userName) {
        Finance finance = financeMapper.selectById(financeId);
        if (finance == null) {
            throw new RuntimeException("融资申请不存在");
        }
        if (!finance.getOwnName().equals(userName)) {
            throw new RuntimeException("无权限撤销此申请");
        }
        if (finance.getStatus() != 0) {
            throw new RuntimeException("只有申请中的记录可以撤销");
        }
        financeMapper.deleteById(financeId);
    }

    @Override
    public Page<Finance> getBankApplications(Integer status, String keyword, String startDate, String endDate, int page, int pageSize) {
        Page<Finance> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Finance> wrapper = new LambdaQueryWrapper<>();

        // 状态过滤（不传则返回全部状态）
        if (status != null) {
            wrapper.eq(Finance::getStatus, status);
        }

        // 关键词：模糊匹配 真实姓名 / 手机号 / 用户名
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(Finance::getRealName, kw)
                    .or().like(Finance::getPhone, kw)
                    .or().like(Finance::getOwnName, kw));
        }

        // 申请时间区间
        LocalDateTime start = parseDateStart(startDate);
        LocalDateTime end = parseDateEnd(endDate);
        if (start != null) {
            wrapper.ge(Finance::getCreateTime, start);
        }
        if (end != null) {
            wrapper.le(Finance::getCreateTime, end);
        }

        wrapper.orderByDesc(Finance::getCreateTime);
        Page<Finance> result = financeMapper.selectPage(pageParam, wrapper);
        fillProductName(result.getRecords());
        return result;
    }

    @Override
    public BankFinanceStats getBankStats() {
        // 数据量较小，一次性拉取后在内存聚合
        LambdaQueryWrapper<Finance> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Finance::getCreateTime);
        List<Finance> all = financeMapper.selectList(wrapper);

        int pendingCount = 0;
        int approvedCount = 0;
        int rejectedCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal monthlyAmount = BigDecimal.ZERO;

        // 本月起止
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();

        List<Finance> pendingList = new ArrayList<>();

        for (Finance f : all) {
            Integer st = f.getStatus();
            BigDecimal money = f.getMoney() == null ? BigDecimal.ZERO : f.getMoney();
            if (st == null || st == 0) {
                pendingCount++;
                pendingList.add(f);
            } else if (st == 1) {
                approvedCount++;
                totalAmount = totalAmount.add(money);
                if (f.getCreateTime() != null && !f.getCreateTime().isBefore(monthStart)) {
                    monthlyAmount = monthlyAmount.add(money);
                }
            } else if (st == 2) {
                rejectedCount++;
                totalAmount = totalAmount.add(money);
            }
        }

        // 待审批：取最早的 5 条（最急需处理）
        pendingList.sort((a, b) -> {
            LocalDateTime ta = a.getCreateTime();
            LocalDateTime tb = b.getCreateTime();
            if (ta == null) return 1;
            if (tb == null) return -1;
            return ta.compareTo(tb);
        });
        if (pendingList.size() > 5) {
            pendingList = new ArrayList<>(pendingList.subList(0, 5));
        }

        // 最近已处理：非申请中的最新 5 条（all 已按 createTime 倒序）
        List<Finance> recentList = all.stream()
                .filter(f -> f.getStatus() != null && f.getStatus() != 0)
                .limit(5)
                .collect(Collectors.toList());

        // 审批通过率 = 已通过 / (已通过 + 已拒绝)
        int denominator = approvedCount + rejectedCount;
        int approvalRate = denominator > 0 ? Math.round((float) approvedCount * 100 / denominator) : 0;

        fillProductName(pendingList);
        fillProductName(recentList);

        BankFinanceStats stats = new BankFinanceStats();
        stats.setPendingCount(pendingCount);
        stats.setApprovedCount(approvedCount);
        stats.setRejectedCount(rejectedCount);
        stats.setTotalAmount(totalAmount);
        stats.setMonthlyAmount(monthlyAmount);
        stats.setApprovalRate(approvalRate);
        stats.setPendingList(pendingList);
        stats.setRecentList(recentList);
        return stats;
    }

    @Override
    public void updateProduct(String userName, Integer productId, FinanceProductRequest request) {
        requireBankRole(userName);
        FinanceProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("融资产品不存在");
        }
        product.setBankName(request.getBankName());
        product.setIntroduce(request.getIntroduce());
        product.setBankPhone(request.getBankPhone());
        product.setMoney(request.getMoney());
        product.setRate(request.getRate());
        product.setRepayment(request.getRepayment());
        productMapper.updateById(product);
    }

    @Override
    public void deleteProduct(String userName, Integer productId) {
        requireBankRole(userName);
        FinanceProduct product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("融资产品不存在");
        }
        productMapper.deleteById(productId);
    }

    // ==================== 私有辅助 ====================

    /** 校验当前用户为银行角色 */
    private void requireBankRole(String userName) {
        User user = userMapper.selectById(userName);
        if (user == null || !"bank".equals(user.getRole())) {
            throw new RuntimeException("只有银行可以管理融资产品");
        }
    }

    /** 批量回填每条申请对应的产品（银行）名称，便于银行工作台展示 */
    private void fillProductName(List<Finance> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Set<Integer> productIds = list.stream()
                .map(Finance::getProductId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        if (productIds.isEmpty()) {
            return;
        }
        List<FinanceProduct> products = productMapper.selectBatchIds(productIds);
        Map<Integer, FinanceProduct> productMap = products.stream()
                .collect(Collectors.toMap(FinanceProduct::getProductId, Function.identity()));
        for (Finance f : list) {
            if (f.getProductId() == null) continue;
            FinanceProduct p = productMap.get(f.getProductId());
            if (p != null) {
                f.setProductName(p.getBankName());
            }
        }
    }

    /** 解析 "yyyy-MM-dd" 为当日 00:00:00，非法返回 null */
    private LocalDateTime parseDateStart(String date) {
        if (date == null || date.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(date.trim()).atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    /** 解析 "yyyy-MM-dd" 为当日 23:59:59，非法返回 null */
    private LocalDateTime parseDateEnd(String date) {
        if (date == null || date.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(date.trim()).atTime(23, 59, 59);
        } catch (Exception e) {
            return null;
        }
    }
}