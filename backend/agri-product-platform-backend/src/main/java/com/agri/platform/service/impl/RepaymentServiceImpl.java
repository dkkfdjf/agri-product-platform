package com.agri.platform.service.impl;

import com.agri.platform.entity.Finance;
import com.agri.platform.entity.Repayment;
import com.agri.platform.mapper.FinanceMapper;
import com.agri.platform.mapper.RepaymentMapper;
import com.agri.platform.service.MessageService;
import com.agri.platform.service.RepaymentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepaymentServiceImpl implements RepaymentService {

    @Autowired
    private RepaymentMapper repaymentMapper;

    @Autowired
    private FinanceMapper financeMapper;

    @Autowired
    private MessageService messageService;

    @Override
    @Transactional
    public void generatePlan(Finance finance) {
        if (finance == null || finance.getFinanceId() == null) {
            return;
        }
        // 幂等：已有计划则跳过
        Long exists = repaymentMapper.selectCount(
                new LambdaQueryWrapper<Repayment>().eq(Repayment::getFinanceId, finance.getFinanceId()));
        if (exists != null && exists > 0) {
            return;
        }

        BigDecimal principal = finance.getMoney() == null ? BigDecimal.ZERO : finance.getMoney();
        int periods = finance.getRepayment() == null ? 0 : finance.getRepayment();
        if (periods <= 0 || principal.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // 月利率 = 年利率% / 12 / 100（rate 字段为百分数，如 5 表示 5%）
        BigDecimal annualRate = finance.getRate() == null ? BigDecimal.ZERO : finance.getRate();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);

        // 等额本息月供 = P*r*(1+r)^n / ((1+r)^n - 1)；r=0 时为 P/n
        BigDecimal monthlyPayment;
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(BigDecimal.valueOf(periods), 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal factor = BigDecimal.ONE.add(monthlyRate); // (1+r)^1
            for (int i = 1; i < periods; i++) {
                factor = factor.multiply(BigDecimal.ONE.add(monthlyRate));
            } // factor = (1+r)^n
            monthlyPayment = principal.multiply(monthlyRate).multiply(factor)
                    .divide(factor.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        }

        LocalDate base = LocalDate.now();
        BigDecimal balance = principal;
        for (int i = 1; i <= periods; i++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPart;
            BigDecimal total;
            if (i == periods) {
                // 末期本金取剩余余额，消除累计舍入误差
                principalPart = balance;
                total = principalPart.add(interest);
            } else {
                principalPart = monthlyPayment.subtract(interest);
                total = monthlyPayment;
            }
            balance = balance.subtract(principalPart);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                balance = BigDecimal.ZERO;
            }

            Repayment r = new Repayment();
            r.setFinanceId(finance.getFinanceId());
            r.setPeriodIndex(i);
            r.setDueDate(base.plusMonths(i));
            r.setPrincipal(principalPart.setScale(2, RoundingMode.HALF_UP));
            r.setInterest(interest);
            r.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
            r.setPaidAmount(BigDecimal.ZERO);
            r.setStatus(0);
            r.setCreateTime(LocalDateTime.now());
            repaymentMapper.insert(r);
        }
    }

    @Override
    public List<Repayment> listByFinance(Integer financeId) {
        LambdaQueryWrapper<Repayment> w = new LambdaQueryWrapper<>();
        w.eq(Repayment::getFinanceId, financeId).orderByAsc(Repayment::getPeriodIndex);
        List<Repayment> list = repaymentMapper.selectList(w);
        LocalDate today = LocalDate.now();
        for (Repayment r : list) {
            boolean od = (r.getStatus() != null && r.getStatus() == 0
                    && r.getDueDate() != null && r.getDueDate().isBefore(today));
            r.setOverdue(od);
        }
        return list;
    }

    @Override
    @Transactional
    public void pay(Integer repaymentId, String userName) {
        Repayment r = repaymentMapper.selectById(repaymentId);
        if (r == null) {
            throw new RuntimeException("还款记录不存在");
        }
        Finance finance = financeMapper.selectById(r.getFinanceId());
        if (finance == null) {
            throw new RuntimeException("融资申请不存在");
        }
        if (!finance.getOwnName().equals(userName)) {
            throw new RuntimeException("无权操作此还款");
        }
        if (r.getStatus() != null && r.getStatus() == 1) {
            throw new RuntimeException("该期已还款");
        }

        r.setStatus(1);
        r.setPaidAmount(r.getTotalAmount());
        r.setPaidTime(LocalDateTime.now());
        repaymentMapper.updateById(r);

        messageService.send(userName, "finance",
                "还款成功",
                "您的融资申请 #" + finance.getFinanceId() + " 第 " + r.getPeriodIndex() + " 期已还款 ¥" + r.getTotalAmount() + "。",
                "/farmer/my-finance");
    }
}
